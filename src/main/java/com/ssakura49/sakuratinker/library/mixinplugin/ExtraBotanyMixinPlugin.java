package com.ssakura49.sakuratinker.library.mixinplugin;

import com.ssakura49.sakuratinker.utils.SafeClassUtil;

public class ExtraBotanyMixinPlugin extends MixinPlugin {
    @Override
    public String[] getRequiredModIds() {
        return new String[]{SafeClassUtil.Modid.ExtraBotany};
    }
}
