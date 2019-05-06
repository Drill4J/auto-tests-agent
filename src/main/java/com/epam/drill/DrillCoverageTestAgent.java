package com.epam.drill;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.ArrayList;

@SuppressWarnings("Convert2Lambda")
public class DrillCoverageTestAgent {
    public static void premain(String drillSession, Instrumentation instrumentation) {
       if(drillSession == null){
           System.out.println("Please provide the scopeId in agentArgs\n" +
                   "-javaagent:/path-to-agent=scopeId");
           return;
       }
        GlobalSpy.self().setSessionId(drillSession);
        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className != null) {
                    try {
                        if (className.equals("java/net/SocketOutputStream")) {
                            CtClass cc = ClassPool.getDefault().get(className.replace("/", "."));
                            CtMethod filter = cc.getMethod("write", "([BII)V");
                            if (filter != null) {
                                filter.insertBefore("byte[] pr = com.epam.drill.GlobalSpy.self().modifyRequestBytes($1,$3);\n" +
                                        "$1 = pr;\n" +
                                        "$3 = com.epam.drill.GlobalSpy.self().calculateLength($3);\n"
                                );
                                return cc.toBytecode();
                            } else return null;
                        } else if (loader != null) {
                            CtClass cc = ClassPool.getDefault().get(className.replace("/", "."));
                            ArrayList<CtMethod> ctMethods = new ArrayList<>();
                            for (CtMethod m : cc.getMethods()) {
                                for (Object an : m.getAnnotations()) {
                                    if (an.toString().equals("@org.junit.jupiter.api.Test") ||
                                            an.toString().equals("@org.junit.jupiter.params.ParameterizedTest")){
                                        ctMethods.add(m);
                                        break;
                                    }
                                }
                            }

                            if (!ctMethods.isEmpty()) {
                                for (CtMethod m : ctMethods) {
                                    m.insertBefore("com.epam.drill.GlobalSpy.self().setTestName(\"" + m.getName() + "\");");
                                }
                                System.out.println("_____________" + className);
                                return cc.toBytecode();
                            } else return null;
                        } else return null;
                    } catch (Throwable ex) {
                        return null;
                    }
                } else
                    return null;

            }
        });


    }
}
