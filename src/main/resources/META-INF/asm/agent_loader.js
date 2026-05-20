var ASMAPI = Java.type('net.minecraftforge.coremod.api.ASMAPI');

function initializeCoreMod() {
    try {
        var AgentBootstrap = Java.type('com.ssakura49.sakuratinker.agent.AgentBootstrap');
        AgentBootstrap.agentLoad();
        print("[SakuraAgent] CoreMod 脚本已成功触发引导程序");
    } catch (e) {
        print("[SakuraAgent] 引导失败: " + e);
    }
    return {};
}