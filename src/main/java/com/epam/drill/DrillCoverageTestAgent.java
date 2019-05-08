package com.epam.drill;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import org.jetbrains.annotations.NotNull;

import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

@SuppressWarnings("Convert2Lambda")
public class DrillCoverageTestAgent {
    private static Logger log = Logger.getLogger("debug");
    public static void premain(String args, Instrumentation instrumentation) {
        HashMap<String, String> paramMap = parseParams(args);
        String sessionId = paramMap.get("sessionId");
        String adminUrl = paramMap.get("adminUrl");
        String agentId = paramMap.get("agentId");

        GlobalSpy.self().setSessionId(sessionId);


        instrumentation.addTransformer(new ClassFileTransformer() {
            @Override
            public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
                if (className != null) {
                    try {
                        if (className.equals("java/net/SocketOutputStream")) {
                            log.info("SOOCKETT!!!!!!!!!!____________________");
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
                            ClassPool cp = ClassPool.getDefault();
                            cp.appendClassPath(new LoaderClassPath(loader));
                            CtClass cc = cp.get(className.replace("/", "."));
                            ArrayList<CtMethod> ctMethods = new ArrayList<>();
                            for (CtMethod m : cc.getMethods()) {
                                for (Object an : m.getAnnotations()) {
                                    if (an.toString().equals("@org.junit.jupiter.api.Test") ||
                                            an.toString().equals("@org.junit.jupiter.params.ParameterizedTest")) {
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

        sendAction(agentId, "START", sessionId, adminUrl);
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run() {
                sendAction(agentId, "STOP", sessionId, adminUrl);
            }
        }));

    }

    @NotNull
    private static HashMap<String, String> parseParams(String drillSession) {
        HashMap<String, String> paramMap = new HashMap<>();
        if (drillSession == null) {
            throw new IllegalArgumentException("Agent should have valid parameters. See spec");
        }
        String[] paramGroups = drillSession.split(",");
        validateParamsGroups(paramGroups);
        for (String paramLine : paramGroups) {
            String[] paramPair = paramLine.split("=");
            validateParamPair(paramPair);
            paramMap.put(paramPair[0], paramPair[1]);
        }
        return paramMap;
    }

    private static void validateParamPair(String[] paramPair) {
        if (paramPair.length != 2)
            throw new IllegalArgumentException("wrong agent parameters");
    }

    private static void validateParamsGroups(String[] paramGroups) {
        if (paramGroups.length < 3)
            throw new IllegalArgumentException("Agent should have 3 parameters. SessionId, AdminUrl and AgentName");
    }

    private static void sendAction(String agentId, String action, String sessionId, String adminUrl) {
        try {
            String authenticate = authenticate(adminUrl);
            URL obj = new URL("http://" + adminUrl + "/api/agents/" + agentId + "/dispatch-action");
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("POST");
            con.setRequestProperty("Authorization", "Bearer " + authenticate);

            con.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(con.getOutputStream());

            //fixme  json hardcode :)
            wr.writeBytes("{\n" +
                    "    \"type\": \"" + action + "\",\n" +
                    "    \"payload\": {\n" +
                    "        \"sessionId\": \"" + sessionId + "\"\n" +
                    "    }\n" +
                    "}");
            wr.flush();
            wr.close();
            System.out.println(con.getResponseCode());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String authenticate(String adminUrl) throws IOException {
        URL obj = new URL("http://" + adminUrl + "/api/login");
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.flush();
        wr.close();
        String authorization = con.getHeaderField("Authorization");
        System.out.println(authorization);
        return authorization;
    }
}
