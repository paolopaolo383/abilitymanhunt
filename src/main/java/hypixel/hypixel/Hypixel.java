package hypixel.hypixel;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public final class Hypixel extends JavaPlugin implements Listener, CommandExecutor
{

    private String runner = "__481926__";
    private static final int rawcnt = 4;
    private String runnerworld="OverWorld";
    private Scoreboard board;
    private Objective obj;
    private Score one;
    boolean isready = false;
    boolean isgaming=false;
    int totaltick=0;
    int min, sec, tick;
    List players;
    ConsoleCommandSender consol = Bukkit.getConsoleSender();
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage(ChatColor.AQUA+"[manhunt] 플러그인 활성화.");


        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(isgaming)
                {
                    players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                    for (int i = 0; i < players.size(); i++)
                    {
                        Player player = (Player) players.get(i);
                        manhuntscoreboard(player);//스코어 보드

                        player.setCompassTarget(getServer().getPlayer(runner).getLocation());


                        runnerworld=String.valueOf(getServer().getPlayer(runner).getLocation().getWorld());
                    }


                    totaltick++;
                    tick++;
                    if (tick==20)
                    {
                        tick=0;
                        sec++;
                    }
                    if (sec==60)
                    {
                        sec = 0;
                        min++;
                    }
                }
                else
                {

                    totaltick = 0;
                    tick=0;
                    sec=0;
                    min=0;

                }
            }

        }.runTaskTimer(this, 0L, 1L);
    }

    public void manhuntscoreboard(Player player)
    {
        int a=0;
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        board = sm.getNewScoreboard();
        obj = board.registerNewObjective("totaltime", "dummy");
        obj.setDisplayName(ChatColor.YELLOW +"MANHUNT");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        one = obj.getScore(ChatColor.WHITE+"총 플레이 시간");
        one.setScore(rawcnt-a);
        a++;


        one = obj.getScore(ChatColor.YELLOW+String.valueOf(min)+"분:"+String.valueOf(sec)+"초");one.setScore(rawcnt-a);a++;
        one.setScore(rawcnt-a);
        a++;

        one = obj.getScore(ChatColor.WHITE+"러너가 있는 월드");
        one.setScore(rawcnt-a);
        a++;
        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world}"))
            one = obj.getScore(ChatColor.GREEN+"overworld");

        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world_nether}"))
            one = obj.getScore(ChatColor.RED+"netherworld");

        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world_the_end}"))
            one = obj.getScore(ChatColor.AQUA+"enderworld");

        one.setScore(rawcnt-a);
        a++;
        player.setScoreboard(board);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, String[] args)
    {
        if(command.getName().equalsIgnoreCase("manhunt"))
        {
            isready = true;
            consol.sendMessage(ChatColor.GREEN +"[MANHUNT] 게임 시작!");
            consol.sendMessage(ChatColor.AQUA+"[MANHUNT] 러너");
            consol.sendMessage(ChatColor.AQUA+"[MANHUNT] 　·"+runner);
            consol.sendMessage(ChatColor.YELLOW+"[MANHUNT] 헌터");
            players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for(int i = 0;i<players.size();i++)
            {
                Player player = (Player)players.get(i);
                if(player.getName().equalsIgnoreCase(runner))//러너
                {
                    player.getInventory().clear();
                    player.sendTitle(ChatColor.RED+"당신은 러너입니다",ChatColor.GREEN+"헌터를 때리면 시작",40,120,40);
                }
                else
                {
                    player.getInventory().clear();
                    consol.sendMessage(ChatColor.YELLOW+"[MANHUNT] 　·"+player.getName());
                    player.sendTitle(ChatColor.YELLOW+"당신은 헌터입니다",ChatColor.GREEN+"ready!",40,120,40);
                }
            }
        }
        return true;
    }


    @EventHandler
    public void EnderDragonDeath(EntityDeathEvent e)
    {
        if(e.getEntity().getType()== EntityType.ENDER_DRAGON&&isgaming)
        {
            isgaming = false;
            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++)
            {
                Player player = (Player) players.get(i);
                player.sendTitle(ChatColor.YELLOW + runner + "승", ChatColor.BLUE + "러너 승!");
            }
        }
    }

    @EventHandler
    public void RunnerDeath(PlayerDeathEvent e)
    {
        e.setDeathMessage(ChatColor.RED+"사람이 죽었다.");
        if(e.getEntity().getPlayer().getName().equalsIgnoreCase(runner)&&isgaming)
        {
            isgaming = false;
            e.setDeathSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
            e.getEntity().getPlayer().setGameMode(GameMode.SPECTATOR);

            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++)
            {
                Player player = (Player) players.get(i);
                player.sendTitle(ChatColor.RED + "러너가 죽었다...", ChatColor.BLUE + "헌터 승!");
            }
        }
    }













    @EventHandler
    public void onHit(EntityDamageByEntityEvent e)
    {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player)
        {
            Player whoHit = (Player) e.getDamager();
            if(whoHit.getPlayer().getName().equalsIgnoreCase(runner))
            {
                if(isready)
                {
                    isready=false;
                    isgaming = true;


                    players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                    for(int i = 0;i<players.size();i++)
                    {
                        Player player = (Player)players.get(i);
                        if(player.getName().equalsIgnoreCase(runner))//러너
                        {
                            player.getInventory().clear();
                            player.sendTitle(ChatColor.RED+"게임시작!",ChatColor.GREEN+"헌터를 피해 엔더 드래곤을 잡으세요",40,120,40);
                        }
                        else
                        {
                            player.getInventory().clear();
                            player.getInventory().addItem(new ItemStack(Material.COMPASS,1));
                            consol.sendMessage(ChatColor.YELLOW+"[MANHUNT] 　·"+player.getName());
                            player.sendTitle(ChatColor.YELLOW+"게임 시작",ChatColor.GREEN+"러너를 잡으세요",40,120,40);
                        }
                    }
                }
            }
            else
            {
                e.setCancelled(true);
            }
        }
    }
}
