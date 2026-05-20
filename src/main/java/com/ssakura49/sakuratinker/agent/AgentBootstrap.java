package com.ssakura49.sakuratinker.agent;

import sun.misc.Unsafe;

import java.io.*;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AgentBootstrap {
    private static final String AGENT_LOADED = "sakura.agent.loaded";
    private static final String AGENT_DONE = "sakura.agent.done";
    private static volatile Instrumentation gInstrumentation = null;

    public static void agentLoad() {
        if (!Boolean.getBoolean(AGENT_DONE)) {
            if (isAgentAlreadyLoaded()) {
                System.out.println("[SakuraAgent] Agent 已加载");
                System.setProperty(AGENT_LOADED, "true");
            } else {
                if (attachAgentDynamically()) {
                    System.out.println("[SakuraAgent] Agent 动态附加成功");
                    System.setProperty(AGENT_LOADED, "true");
                } else {
                    System.err.println("[SakuraAgent] Agent 动态附加失败");
                }
            }
        }
    }

    private static boolean isAgentAlreadyLoaded() {
        if (Boolean.getBoolean(AGENT_LOADED)) {
            return true;
        } else {
            for(String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
                if (arg.contains("-javaagent:") && arg.contains("sakura_agent.jar")) {
                    System.out.println("[SakuraAgent] Agent 启动参数: " + arg);
                    System.setProperty(AGENT_LOADED, "true");
                    return true;
                }
            }

            if (AgentBridge.isAgentReady()) {
                System.setProperty(AGENT_LOADED, "true");
                return true;
            } else {
                return false;
            }
        }
    }

    private static boolean attachAgentDynamically() {
        try {
            Class.forName("com.sun.tools.attach.VirtualMachine");
        } catch (ClassNotFoundException var17) {
            System.err.println("[SakuraAgent] VirtualMachine 类不可用");
            return false;
        }
        Object vm = null;
        boolean var20;
        try {
            Path agentPath = extractAgentJar();
            if (agentPath != null && Files.exists(agentPath)) {
                String pid = Long.toString(ProcessHandle.current().pid());
                System.out.println("[SakuraAgent] 当前进程 PID: " + pid);
                enableSelfAttach();
                Class<?> vmClass = Class.forName("com.sun.tools.attach.VirtualMachine");
                vm = vmClass.getMethod("attach", String.class).invoke((Object)null, pid);
                vmClass.getMethod("loadAgent", String.class, String.class).invoke(vm, agentPath.toAbsolutePath().toString(), "");

                boolean success = false;
                for (int i = 0; i < 20; i++) {
                    if ("true".equals(System.getProperty("sakura.agent.ready"))) {
                        success = true;
                        break;
                    }
                    Thread.sleep(100);
                }

                if (success) {
                    System.setProperty(AGENT_LOADED, "true");
                    //System.out.println("[SakuraAgent] Agent 动态附加成功");
                    return true;
                }
                //                Thread.sleep(1000L);
//                boolean success = AgentBridge.isAgentReady() || gInstrumentation != null;
//                if (success) {
//                    System.out.println("[SakuraAgent] Agent 动态附加成功");
//                }

                boolean var5 = success;
                return var5;
            }
            System.err.println("[SakuraAgent] Agent JAR 提取失败");
            var20 = false;
        } catch (Exception e) {
            System.err.println("[SakuraAgent] 动态附加异常: " + e.getMessage());
            e.printStackTrace();
            var20 = false;
            return var20;
        } finally {
            if (vm != null) {
                try {
                    Class<?> vmClass = Class.forName("com.sun.tools.attach.VirtualMachine");
                    vmClass.getMethod("detach").invoke(vm);
                    System.out.println("[SakuraAgent] VirtualMachine 已分离");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        return var20;
    }

    private static void enableSelfAttach() {
        try {
            Class<?> hotSpotVMClass = Class.forName("sun.tools.attach.HotSpotVirtualMachine");
            Field allowAttachSelfField = hotSpotVMClass.getDeclaredField("ALLOW_ATTACH_SELF");
            Unsafe unsafe = getUnsafe();
            Object base = unsafe.staticFieldBase(allowAttachSelfField);
            long offset = unsafe.staticFieldOffset(allowAttachSelfField);
            unsafe.putBoolean(base, offset, true);
            System.out.println("[SakuraAgent] 启用自我附加");
        } catch (Exception e) {
            System.err.println("[SakuraAgent] 启用自我附加失败: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static Unsafe getUnsafe() throws Exception {
        Field unsafeField = Unsafe.class.getDeclaredField("theUnsafe");
        unsafeField.setAccessible(true);
        return (Unsafe)unsafeField.get((Object)null);
    }

    private static Path extractAgentJar() {
        try {
            String agentFileName = "sakura_agent.jar";
            String resourcePath = "assets/sakuratinker/agent/" + agentFileName;
            Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "sakuratinker");
            if (!Files.exists(tempDir)) {
                Files.createDirectories(tempDir);
            }

            Path outputPath = tempDir.resolve(agentFileName);
            Path versionFile = tempDir.resolve("sakura-agent.version");
            InputStream resourceStream = getResourceStream(resourcePath);
            if (resourceStream == null) {
                System.err.println("[SakuraAgent] 无法找到 Agent JAR: " + resourcePath);
                return null;
            } else {
                String currentHash = calculateHash(resourceStream);
                resourceStream.close();
                boolean needsUpdate = true;
                if (Files.exists(outputPath, new LinkOption[0]) && Files.exists(versionFile, new LinkOption[0])) {
                    try {
                        String savedHash = (new String(Files.readAllBytes(versionFile))).trim();
                        if (savedHash.equals(currentHash)) {
                            needsUpdate = false;
                        }
                    } catch (Exception var16) {
                        System.out.println("[SakuraAgent] 无法读取版本信息，重新提取");
                    }
                }

                if (needsUpdate) {
                    if (Files.exists(outputPath, new LinkOption[0])) {
                        try {
                            Files.delete(outputPath);
                        } catch (Exception e) {
                            System.err.println("[SakuraAgent] 无法删除旧文件: " + e.getMessage());
                            outputPath.toFile().deleteOnExit();
                        }
                    }

                    resourceStream = getResourceStream(resourcePath);
                    if (resourceStream == null) {
                        System.err.println("[SakuraAgent] 无法重新获取 Agent JAR");
                        return null;
                    }

                    try {
                        Files.copy(resourceStream, outputPath, StandardCopyOption.REPLACE_EXISTING);
                        System.out.println("[SakuraAgent] Agent 已提取到: " + outputPath);
                        Files.write(versionFile, currentHash.getBytes());
                    } finally {
                        resourceStream.close();
                    }
                }

                return outputPath;
            }
        } catch (IOException e) {
            System.err.println("[SakuraAgent] 提取 Agent 失败: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private static InputStream getResourceStream(String resourcePath) {
        InputStream in = AgentBootstrap.class.getClassLoader().getResourceAsStream(resourcePath);
        if (in == null) {
            in = ClassLoader.getSystemClassLoader().getResourceAsStream(resourcePath);
        }

        if (in == null) {
            in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }

        if (in == null) {
            String pathWithSlash = "/" + resourcePath;
            in = AgentBootstrap.class.getResourceAsStream(pathWithSlash);
        }

        if (in == null) {
            Path devPath = Paths.get("src/main/resources", resourcePath);
            if (Files.exists(devPath)) {
                try {
                    in = Files.newInputStream(devPath);
                } catch (IOException e) {
                    System.err.println("[SakuraAgent] 无法从开发环境加载: " + e.getMessage());
                }
            }
        }

        if (in == null) {
            Path buildPath = Paths.get("build/resources/main", resourcePath);
            if (Files.exists(buildPath)) {
                try {
                    in = Files.newInputStream(buildPath);
                } catch (IOException e) {
                    System.err.println("[SakuraAgent] 无法从构建目录加载: " + e.getMessage());
                }
            }
        }

        return in;
    }

    private static String calculateHash(InputStream inputStream) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] buffer = new byte[8192];

            int bytesRead;
            while((bytesRead = inputStream.read(buffer)) != -1) {
                digest.update(buffer, 0, bytesRead);
            }

            byte[] hashBytes = digest.digest();
            StringBuilder hexString = new StringBuilder();

            for(byte b : hashBytes) {
                String hex = Integer.toHexString(255 & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();
        } catch (Exception e) {
            System.err.println("[SakuraAgent] 计算哈希失败: " + e.getMessage());
            return String.valueOf(System.currentTimeMillis());
        }
    }

    public static Instrumentation getInstrumentation() {
        return gInstrumentation;
    }
}
