package com.epam.drill;

@SuppressWarnings("ALL")
public class GlobalSpy {
    private static GlobalSpy inst = new GlobalSpy();
    private String drillSession;

    public static GlobalSpy self() {
        return inst;
    }

    public ThreadLocal<String> testNameStorage = new ThreadLocal<String>();

    public void setTestName(String name) {
        testNameStorage.set(name);
    }

    private GlobalSpy() {
    }

    public String getTestName() {
        return testNameStorage.get();
    }

    public boolean hasTestName() {
        return testNameStorage.get() != null;
    }

    public byte[] modifyRequestBytes(byte[] rawRequestBytes, int len) {
        if (hasTestName()) {
            String rawRequest = new String(rawRequestBytes);
            if (isHttpRequest(rawRequest)) {
                String requestLine = generateSpyHeaders();
                String s = rawRequest.replaceFirst("\n", "\n" + requestLine + "\n");
                return s.getBytes();
            } else return rawRequestBytes;
        } else return rawRequestBytes;

    }

    private String generateSpyHeaders() {
        return "DrillTestName: " + getTestName() + "\n" + "DrillSessionId: " + drillSession;
    }

    public int calculateLength(int len) {
        if (!com.epam.drill.GlobalSpy.self().hasTestName()) {
            return len;
        }
        return len + (generateSpyHeaders()).getBytes().length + 1;
    }

    private boolean isHttpRequest(String rawRequest) {
        return rawRequest.startsWith("OPTIONS ") ||
                rawRequest.startsWith("GET ") ||
                rawRequest.startsWith("HEAD ") ||
                rawRequest.startsWith("POST ") ||
                rawRequest.startsWith("PUT ") ||
                rawRequest.startsWith("PATCH ") ||
                rawRequest.startsWith("DELETE ") ||
                rawRequest.startsWith("TRACE ") ||
                rawRequest.startsWith("CONNECT ");
    }


    public void setSessionId(String drillSession) {
        this.drillSession = drillSession;
    }
}
