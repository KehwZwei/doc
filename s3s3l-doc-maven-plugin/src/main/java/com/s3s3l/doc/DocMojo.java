package com.s3s3l.doc;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.codehaus.plexus.util.StringUtils;

import com.s3s3l.niflheim.DataNode;
import com.s3s3l.niflheim.DataType;
import com.s3s3l.niflheim.DocBook;
import com.s3s3l.niflheim.Http;
import com.s3s3l.niflheim.Interface;
import com.s3s3l.niflheim.NiflheimDoc;
import com.s3s3l.niflheim.Param;
import com.s3s3l.niflheim.RPC;

/**
 * 
 * <p>
 * </p>
 * ClassName: DocMojo <br>
 * date: Jun 25, 2018 7:01:04 PM <br>
 * 
 * @author kehw_zwei
 * @version 1.0.0
 * @since JDK 1.8
 */
@Mojo(name = "doc", defaultPhase = LifecyclePhase.PACKAGE)
public class DocMojo extends AbstractMojo {

    @Parameter(defaultValue = "${project.build.outputDirectory}")
    private String outputDirectory;
    @Parameter(defaultValue = "${project.build.directory}")
    private String directory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        try {
            scanClasses(new File(outputDirectory));
        } catch (IOException e) {
            throw new MojoExecutionException("Doc Mojo Exception", e);
        }
    }

    private void scanClasses(File file) throws IOException {

        try (URLClassLoader cl = new URLClassLoader(new URL[] { new File(outputDirectory).toURI()
                .toURL() }, DocMojo.class.getClassLoader())) {
            if (file.isFile()) {
                getLog().info(file.getPath());
                try {
                    // checkAnnotation(file, cl);
                    createDocBook(cl.loadClass(pathToPackage(file.getPath())));
                } catch (Exception e) {
                    getLog().warn(String.format("Error to create doc for %s", file.getAbsolutePath()), e);
                }
            } else {
                for (File _file : file.listFiles()) {
                    scanClasses(_file);
                }
            }
        }
    }

    private DocBook createDocBook(Class<?> cls) {
        if (null == cls || !cls.isAnnotationPresent(NiflheimDoc.class)) {
            return null;
        }
        DocBook book = new DocBook();
        NiflheimDoc niflheimDoc = cls.getAnnotation(NiflheimDoc.class);
        book.setName(getFirstValidString(niflheimDoc.name(), niflheimDoc.value()));
        for (Method method : cls.getMethods()) {
            Doc doc = createDoc(method, cls);
            if (doc != null) {
                book.appendDoc(doc);
            }
        }

        return book;
    }

    private Doc createDoc(Method method, Class<?> cls) {
        if (null == method || !method.isAnnotationPresent(Interface.class)) {
            return null;
        }
        Doc doc = new Doc();
        Interface iface = method.getAnnotation(Interface.class);
        doc.setName(getFirstValidString(iface.name(), iface.value()));
        doc.setDesc(iface.desc());
        doc.setRequestType(iface.requestType());

        switch (doc.getRequestType()) {
            case HTTP_OR_HTTPS:
                if (!method.isAnnotationPresent(Http.class)) {
                    getLog().warn("Request type `HTTP_OR_HTTPS` must be annotationed by `Http`");
                    break;
                }
                Http http = method.getAnnotation(Http.class);
                doc.setHttpMethod(http.method());
                doc.setPath(getFirstValidString(http.path(), http.value()));
                break;
            case RPC:
                if (!cls.isAnnotationPresent(RPC.class)) {
                    getLog().warn("Request type `RPC` must be annotationed by `RPC`");
                    break;
                }
                RPC rpc = cls.getAnnotation(RPC.class);
                doc.setPath(rpc.path());
                break;
            default:
                break;
        }
        return doc;
    }

    private List<DataNode> createRPCRequestNode(Method method, Class<?> cls, String scope) {
        List<DataNode> request = new ArrayList<>();

        DataNode soa = new DataNode("soa", "soa信息", DataType.OBJECT);
        soa.appendChildren(new DataNode("rpc", "rpc版本", DataType.STRING, "1.1"));

        DataNode metas = new DataNode("metas", "meta信息", DataType.OBJECT);
        metas.appendChildren(new DataNode("ServiceTimeOut", "超时时间", DataType.STRING, "3000"));
        metas.appendChildren(new DataNode("WEAK_CALL", "", DataType.STRING, "FALSE"));

        DataNode args = new DataNode("args", "接口参数", DataType.OBJECT);
        java.lang.reflect.Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            java.lang.reflect.Parameter parameter = parameters[i];
            DataNode node;
            if (!parameter.isAnnotationPresent(Param.class)) {
                node = new DataNode("arg" + i, parameter.getName(), DataType.JSON);
            } else {
                Param param = parameter.getAnnotation(Param.class);
                node = new DataNode("arg" + i,
                        getFirstValidString(param.desc(), param.name(), param.value(), parameter.getName()),
                        DataType.JSON, param.remark());
            }

            args.appendChildren(node);
        }

        request.add(new DataNode("ver", "soa版本", DataType.STRING, "1.0"));
        request.add(new DataNode("iface", "服务接口", DataType.STRING, cls.getName()));
        request.add(new DataNode("method", "服务方法", DataType.STRING, method.getName()));
        request.add(args);
        request.add(soa);
        request.add(metas);

        return request;
    }

    private List<DataNode> toDataNode(Class<?> cls) {
        if (cls == null) {
            return Collections.emptyList();
        }

        List<DataNode> params = new ArrayList<>();

        for (Field field : cls.getFields()) {
            if (!field.isAnnotationPresent(Param.class)) {
                continue;
            }

            Param param = field.getAnnotation(Param.class);

            DataNode node = new DataNode(getFirstValidString(param.name(), param.value(), field.getName()),
                    param.desc(), getDataType(cls), param.remark());
        }
        return params;
    }

    private DataType getDataType(Class<?> cls) {
        if (Number.class.isAssignableFrom(cls) || int.class.isAssignableFrom(cls) || short.class.isAssignableFrom(cls)
                || float.class.isAssignableFrom(cls) || double.class.isAssignableFrom(cls)) {
            return DataType.NUMBER;
        } else if (Boolean.class.isAssignableFrom(cls) || boolean.class.isAssignableFrom(cls)) {
            return DataType.BOOLEAN;
        } else if (String.class.isAssignableFrom(cls)) {
            return DataType.STRING;
        } else if (List.class.isAssignableFrom(cls) || Set.class.isAssignableFrom(cls) || cls.isArray()) {
            return DataType.ARRAY;
        } else if (Byte.class.isAssignableFrom(cls) || byte.class.isAssignableFrom(cls)) {
            return DataType.BYTE;
        } else if (!cls.isPrimitive() && Object.class.isAssignableFrom(cls)) {
            return DataType.OBJECT;
        }

        return DataType.OBJECT;
    }

    public static void main(String[] args) {
        System.out.println(Integer.class.isPrimitive());
        System.out.println(Number.class.isAssignableFrom(Integer.class));
        System.out.println(Number.class.isAssignableFrom(int.class));
    }

    private String getFirstValidString(String... strings) {
        for (String string : strings) {
            if (!StringUtils.isBlank(string)) {
                return string;
            }
        }

        return null;
    }

    @SuppressWarnings("unused")
    private void checkAnnotation(File file, ClassLoader cl) throws ClassNotFoundException, IOException {
        Class<?> cls = cl.loadClass(pathToPackage(file.getPath()));
        for (Annotation annotation : cls.getAnnotations()) {
            getLog().info(annotation.toString());
        }
    }

    private String pathToPackage(String path) {
        path = path.substring(outputDirectory.length(), path.indexOf(".class"))
                .replaceAll("/", ".");
        if (path.startsWith(".")) {
            path = path.replaceFirst(".", "");
        }

        return path;
    }
}
