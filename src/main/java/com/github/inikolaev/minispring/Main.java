package com.github.inikolaev.minispring;

import javax.inject.Named;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Objects;

class ClassNameComparator implements Comparator<Class> {
    private ClassNameComparator() {}

    public int compare(Class l, Class r) {
        return l.getSimpleName().compareTo(r.getSimpleName());
    }

    public static final Comparator<Class> theInstance = new ClassNameComparator();
}

public class Main {
    public static void main(String[] args) throws IOException {
        final Context context = new Context();
        context.registerBean("firstName", "Igor");
        context.registerBean("lastName", "Nikolaev");
        scan(context, "com.github.inikolaev");
        //context.registerBean("truckDriver", Person.class);
        //context.registerBean("truck", Truck.class);
        System.out.println(context.get("truckDriver").toString());
        System.out.println(context.get("truck").toString());

    }

    public static void scan(Context context, String packageName) throws IOException {
        final String packagePath = packageName.replaceAll("\\.", "/");
        final ClassLoader classLoader = Main.class.getClassLoader();
        final Enumeration<URL> resources = classLoader.getResources(packagePath);

        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            //resource.
            Files.walk(new File(resource.getFile()).toPath())
                    .map(Path::toString)
                    .filter(path -> path.endsWith(".class"))
                    .map(path -> path.substring(path.indexOf(packagePath)))
                    .map(path -> path.replace(".class", ""))
                    .map(path -> path.replaceAll("/", "."))
                    .map(className -> {
                        try {
                            return classLoader.loadClass(className);
                        } catch (ClassNotFoundException e) {
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .filter(clazz -> {
                        final Named named = clazz.getDeclaredAnnotation(Named.class);
                        return named != null && named.value().trim().length() > 0;
                    })
                    .sorted(ClassNameComparator.theInstance) // this is a hack
                    .forEach(clazz -> {
                        System.out.println(clazz);

                        final Named named = clazz.getDeclaredAnnotation(Named.class);
                        context.registerBean(named.value(), clazz);
                    });
        }
    }
}
