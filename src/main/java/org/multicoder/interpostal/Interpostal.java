package org.multicoder.interpostal;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import org.multicoder.interpostal.common.ModRegsitries;
import org.multicoder.interpostal.common.commands.PostalCommands;
import org.multicoder.interpostal.common.commands.PostalData;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Mod(Interpostal.MODID)
public class Interpostal
{
    public static PostalData POST;
    public static final String MODID = "interpostal";
    private static final Logger LOGGER = LogUtils.getLogger();

    public Interpostal(IEventBus modEventBus, ModContainer ignored)
    {
        LOGGER.info("Interpostal Version: 2.0.0");
        modEventBus.addListener(this::commonSetup);
        ModRegsitries.BLOCKS.register(modEventBus);
        ModRegsitries.ITEMS.register(modEventBus);
        ModRegsitries.CREATIVE_MODE_TABS.register(modEventBus);
        NeoForge.EVENT_BUS.register(this);
        modEventBus.addListener(this::addCreative);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }


    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {

    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        ServerLevel Overworld = Objects.requireNonNull(event.getServer().getLevel(Level.OVERWORLD));
        POST = Objects.requireNonNull(Overworld.getDataStorage().computeIfAbsent(new SavedData.Factory<>(PostalData::create,PostalData::load),"postalData"));
        POST.setDirty();
    }

    @EventBusSubscriber(modid = MODID,bus = EventBusSubscriber.Bus.GAME)
    public static class GameEvents
    {
        @SubscribeEvent
        private static void registerCommands(RegisterCommandsEvent event)
        {
            PostalCommands.RegsiterCommands(event.getDispatcher());
        }
        @SubscribeEvent
        private static void playerJoin(PlayerEvent.PlayerLoggedInEvent event)
        {
            ServerPlayer player = (ServerPlayer) event.getEntity();
            boolean Exists = POST.PlayersPost.stream().anyMatch(tag -> {CompoundTag ct = (CompoundTag) tag;String PlayerName = ct.getString("Player");return PlayerName.equals(player.getName().getString());});
            if(!Exists)
            {
                CompoundTag T = new CompoundTag();
                T.putString("Player",player.getName().getString());
                T.put("Post",new ListTag());
                POST.PlayersPost.add(T);
                POST.setDirty();
            }
            else
            {
                AtomicReference<CompoundTag> CT = new AtomicReference<>();
                POST.PlayersPost.forEach(tag -> {CompoundTag T = (CompoundTag) tag;if(T.getString("Player").equals(player.getName().getString())){CT.set(T);}});
                CompoundTag Post = CT.get();
                int Count = Post.getList("Post", Tag.TAG_COMPOUND).size();
                player.sendSystemMessage(Component.translatable("text.interpostal.postcount",Count));
            }
        }
    }

    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        private static void onClientSetup(FMLClientSetupEvent event)
        {

        }
    }
}
