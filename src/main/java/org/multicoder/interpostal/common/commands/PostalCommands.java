package org.multicoder.interpostal.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.multicoder.interpostal.Interpostal;

import java.util.Objects;

public class PostalCommands
{
    public static void RegsiterCommands(CommandDispatcher<CommandSourceStack> dispatcher)
    {
        dispatcher.register(Commands.literal("postal").then(Commands.literal("version").executes(PostalCommands::VersionCheck))).createBuilder().build();
        dispatcher.register(Commands.literal("postal").then(Commands.literal("read").executes(PostalCommands::ReadPost))).createBuilder().build();
    }

    private static int ReadPost(CommandContext<CommandSourceStack> context)
    {
        ServerPlayer Player = Objects.requireNonNull(context.getSource().getPlayer());
        CompoundTag Post = Interpostal.POST.FetchPLBox(Player);
        ListTag Items = Post.getList("Post", Tag.TAG_COMPOUND);
        CompoundTag Item = Items.getCompound(0);
        String Sender = Item.getString("From");
        String Message = Item.getString("Message");
        if(Item.contains("Gift"))
        {
            ItemStack Gift = ItemStack.parseOptional(Player.registryAccess(),Item.getCompound("Gift"));
            Player.addItem(Gift);
        }
        Player.sendSystemMessage(Component.translatable("text.interpostal.postmessage",Sender,Message));
        Items.remove(Item);
        Post.put("Post",Items);
        Interpostal.POST.setDirty();
        return 0;
    }

    private static int VersionCheck(CommandContext<CommandSourceStack> context)
    {
        Objects.requireNonNull(context.getSource().getPlayer()).sendSystemMessage(Component.literal("Interpostal Version: 2.0.0"));
        return 0;
    }
}
