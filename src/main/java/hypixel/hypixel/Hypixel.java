package hypixel.hypixel;
import com.destroystokyo.paper.HeightmapType;
import com.destroystokyo.paper.event.server.ServerTickStartEvent;
import io.papermc.paper.world.MoonPhase;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.util.Ticks;
import org.bukkit.*;
import com.google.common.util.concurrent.FutureCallback;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.logging.Logger;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.block.Block;
import org.bukkit.WorldCreator.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.DragonBattle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.material.MaterialData;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.WorldCreator.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Consumer;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import org.checkerframework.checker.units.qual.C;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
public final class Hypixel extends JavaPlugin implements Listener
{
    private Scoreboard board;
    private Objective obj;
    private Score one;
    private Score two;
    private Score three;
    private Score four;
    private Score five;
    private Score six;
    private Score seven;
    int totaltick=0;
    boolean isdropinghead = false;
    boolean isgaming=false;
    String game="none";
    int min, sec, tick;
    ConsoleCommandSender consol = Bukkit.getConsoleSender();
    HashMap<UUID, Integer> hack = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> diamond = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> stone = new HashMap<UUID, Integer>();
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage( ChatColor.AQUA + "[하이픽셀 플러그인 활성화.]");
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(isgaming)
                {
                    totaltick++;
                    tick++;
                    if (tick==20)
                    {
                        tick=0;
                        sec++;
                    }
                    if (sec==60)
                    {
                        sec=0;
                        min++;
                    }


                    if(min==10&&sec==0&&tick==0)
                    {
                        isdropinghead = true;
                        getServer().getWorld("uhc").getWorldBorder().setSize(50, 2000);
                        List play = getServer().getWorld("uhc").getPlayers();
                        int q = play.size();
                        for(int i = 0;i<q;i++)
                        {
                            Player pl = (Player) play.get(i);
                            pl.sendTitle("자기장이 줄어듭니다","평화시간 끝", 0,40,20);
                        }
                    }
                    if(min==45&&sec==0&&tick==0)
                    {
                        isdropinghead = true;
                        List play = getServer().getWorld("uhc").getPlayers();
                        int q = play.size();
                        for(int i = 0;i<q;i++)
                        {

                            Player pl = (Player) play.get(i);
                            pl.sendTitle("자기장이 줄어듭니다","독 효과가 부여됩니다", 0,40,20);
                            if(pl.getGameMode()==GameMode.SURVIVAL)
                            {
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 1000000000,2));
                            }

                        }
                        getServer().getWorld("uhc").getWorldBorder().setSize(5, 180);
                        //타이틀
                    }


                }
                else
                {

                    totaltick = 0;
                    //getServer().getWorld("world").getWorldBorder().setSize(3000);
                    tick=0;
                    sec=0;
                    min=0;
                }
            }

        }.runTaskTimer(this, 0L, 1L);
    }






    public void uhcscboard(Player player)
    {
        int p = 1;
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        board = sm.getNewScoreboard();
        obj = board.registerNewObjective("totalplaytime", "dummy");
        obj.setDisplayName(ChatColor.AQUA +"UHC");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);


        one = obj.getScore(ChatColor.GREEN+String.valueOf(min)+":"+String.valueOf(sec));
        one.setScore(p);
        p++;
        two = obj.getScore(ChatColor.WHITE+"총 플레이 시간");
        two.setScore(p);
        p++;
        if(min<10)
        {
            int ti = 600-totaltick/20;

            three = obj.getScore(ChatColor.GREEN+String.valueOf((int)(ti/60))+":"+String.valueOf((ti%60)));
        }
        else if(min<45)
        {
            int ti = 2700-totaltick/20;

            three = obj.getScore(ChatColor.GREEN+String.valueOf((int)(ti/60))+":"+String.valueOf((ti%60)));
        }
        else if(min<50)
        {
            int ti = 3000-totaltick/20;

            three = obj.getScore(ChatColor.GREEN+String.valueOf((int)(ti/60))+":"+String.valueOf((ti%60)));
        }
        three.setScore(p);
        p++;
        if(min<10)
        {
            four= obj.getScore(ChatColor.WHITE+"평화시간 끝나기");
        }
        else if(min<45)
        {
            four= obj.getScore(ChatColor.WHITE+"데스메치까지");
        }
        else if(min<50)
        {
            four= obj.getScore(ChatColor.WHITE+"게임 끝까지");
        }
        four.setScore(p);
        p++;
        player.setScoreboard(board);
    }





    @EventHandler
    public void leave(PlayerQuitEvent e)
    {

        e.setQuitMessage("아... 그는 갔습니다.");
        if(e.getPlayer().getGameMode()==GameMode.SURVIVAL)
        {

            if(isgaming)
            {
                e.getPlayer().setHealth(0);
            }
        }

    }
    @EventHandler
    public void dead(PlayerDeathEvent e)
    {

        getServer().sendMessage(Component.text(ChatColor.RED+"사람이 죽었다"));
        if(e.getEntity().getType()==EntityType.PLAYER)
        {
            if(isdropinghead&&isgaming)
            {
                getServer().getWorld("uhc").dropItemNaturally(e.getEntity().getLocation(),new ItemStack(Material.PLAYER_HEAD,1));
                e.getEntity().setGameMode(GameMode.SPECTATOR);
            }

        }


    }
    @EventHandler
    public void join(PlayerJoinEvent e)
    {
        consol.sendMessage( ChatColor.YELLOW + "[사람이 들어옴]");
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        diamond.put(uuid,0);
        stone.put(uuid,0);
        hack.put(uuid,0);
        e.setJoinMessage("누군가 들어왔다!!!");
        if(game=="uhc"&&isgaming&&isdropinghead)
        {
            player.setGameMode(GameMode.SPECTATOR);
        }
        else if(!isgaming)
        {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().teleport(getServer().getWorld("world").getHighestBlockAt(0, 0).getLocation().add(0,1,0));
        }


        if(player.hasResourcePack())
        {

        }
        else if(!player.hasResourcePack())
        {
            player.sendMessage(Component.text("리소스팩이 없습니다. 이런 리소스펙은 어떠세요?"));
            player.setResourcePack("https://blogattach.naver.com/ff6ae356407475c0ef0b645a6281fe8c2c768fe1/20210721_258_blogfile/481926paolo_1626870902838_87x7aT_zip/VanillaTweaks_r260907.zip");
        }

    }
    @EventHandler
    public void playerchat(PlayerChatEvent e)
    {
        e.setCancelled(true);
    }
    @EventHandler
    public void player(BlockBreakEvent e)
    {

        double rate;
        UUID uuid = e.getPlayer().getUniqueId();
        if(e.getBlock().getType()==Material.DIAMOND_ORE)
        {
            diamond.put(uuid,diamond.get(uuid)+1);
            rate = diamond.get(uuid)/(float)stone.get(uuid);
            if((rate*100)>5.2&&min>10&&game=="uhc")
            {
                //핵 판정
                e.getPlayer().kick(Component.text("핵 쓰지 마세요!"));

            }
        }
        if(e.getBlock().getType()==Material.STONE)
        {
            stone.put(uuid,stone.get(uuid)+1);
        }
    }
    @Override
    public void onDisable()
    {

    }



    @Override
    public boolean onCommand(CommandSender sender, Command command,String s,  String[] args)
    {
        Player player = (Player) sender;
        if(command.getName().equalsIgnoreCase("uhc"))
        {
            sender.sendMessage(ChatColor.AQUA+"[Hypixel] 월드 생성중");
            UHCWorldcreater();
        }
        return true;
    }
























    public void UHCWorldcreater()
    {

        WorldCreator seed = new WorldCreator("uhc");
        World world = seed.createWorld();
        List players = getServer().getWorld("world").getPlayers();

        for (int i = 0;i<players.size();i++)
        {
            //월드 세틸
            world.setPVP(false);
            world.setDifficulty(Difficulty.PEACEFUL);
            world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            world.setGameRule(GameRule.NATURAL_REGENERATION,false);
            world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);


            world.getWorldBorder().setCenter(0,0);
            world.getWorldBorder().setDamageAmount(2);
            world.getWorldBorder().setWarningDistance(100);
            world.getWorldBorder().setDamageBuffer(0);
            world.getWorldBorder().setSize(1000);


            //플레이어 세팅
            Player pl = (Player)players.get(i);
            Random createRandom = new Random();
            int xi = createRandom.nextInt(1000);
            xi-=500;
            int zi = createRandom.nextInt(1000);
            zi-=500;
            pl.teleport(world.getHighestBlockAt(xi, zi).getLocation().add(0,1,0));


            pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            pl.setGameMode(GameMode.SURVIVAL);


            pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,300, 255,true));
            pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE,12000, 255,true));


            pl.getInventory().clear();
            ItemStack firstitem = new ItemStack(Material.STONE_PICKAXE,1);
            pl.getInventory().addItem(firstitem);
            firstitem = new ItemStack(Material.STONE_AXE,1);
            pl.getInventory().addItem(firstitem);
            firstitem = new ItemStack(Material.STONE_SWORD,1);
            pl.getInventory().addItem(firstitem);
            firstitem = new ItemStack(Material.STONE_SHOVEL,1);
            pl.getInventory().addItem(firstitem);



            //플러그인 변수 세팅

            game = "uhc";
            isdropinghead = false;




        }
        isgaming = true;

    }
}
