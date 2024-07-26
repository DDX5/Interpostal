package org.multicoder.interpostal.common.commands;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicReference;

public class PostalData extends SavedData
{
    public ListTag PlayersPost;

    public static PostalData create()
    {
        PostalData D = new PostalData();
        D.PlayersPost = new ListTag();
        return D;
    }
    public CompoundTag FetchPLBox(ServerPlayer player)
    {
        AtomicReference<CompoundTag> PLBox = new AtomicReference<>();
        PlayersPost.forEach(tag -> {
            CompoundTag CT = (CompoundTag) tag;
            if(CT.getString("Player").equals(player.getName().getString())){
                PLBox.set(CT);
            }
        });
        return PLBox.get();
    }
    public static PostalData load(CompoundTag tag, HolderLookup.Provider ignored)
    {
        PostalData data = create();
        data.PlayersPost = tag.getList("plbox", Tag.TAG_COMPOUND);
        return data;
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag compoundTag, HolderLookup.@NotNull Provider provider)
    {
        compoundTag.put("plbox",PlayersPost);
        return compoundTag;
    }
}
