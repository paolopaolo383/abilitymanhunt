package hypixel.hypixel;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.time.Instant;
import java.util.*;

public final class Hypixel extends JavaPlugin implements Listener, CommandExecutor
{




    private Inventory inv;
    private String runner = "__481926__";
    private static final int rawcnt = 6;
    private String runnerworld="OverWorld";
    private Scoreboard board;
    private Objective obj;
    private int loctop = 0;
    
    private Score one;
    private Player run;
    boolean isready = false;
    boolean isgaming=false;
    int totaltick=0;
    
    boolean itemclick=false;
    Material item = null;
    boolean ispoison = false;
    int min, sec, tick;
    private List players;
    int compass=0;

    private boolean isrebirthable=false;
    int maxhealth = 20;
    int cool=0;
    int compasscool=0;
    String skill ="";
    List<String> skills = new ArrayList<String>();
    
    
    
    
    List<String> koreaskill=new ArrayList<String>();
    List<String> engskill  = new ArrayList<String>();
    List<UUID> deadplayer=new ArrayList<UUID>();
    
    List<UUID> warnplayer=new ArrayList<UUID>();
    org.bukkit.Location loc[]=  new org.bukkit.Location[500];
    private static List<UUID> quitplayer=new ArrayList<UUID>();
    
    HashMap<UUID, Integer> quitcooltime = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> quitcnt = new HashMap<UUID, Integer>();
    int cooltime = 0;
    ConsoleCommandSender consol = Bukkit.getConsoleSender();
    HashMap<UUID, Boolean> isparty = new HashMap<UUID, Boolean>();
    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage(ChatColor.AQUA+"[manhunt] 플러그인 활성화.");


        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(isgaming)
                {
                    try {
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            UUID id = player.getUniqueId();
                            manhuntscoreboard(player);//스코어 보드

                            if (compass == 0) {
                                player.setCompassTarget(getServer().getPlayer(runner).getLocation());

                            }

                            if (ispoison && tick == 19 && !player.getName().equalsIgnoreCase(runner) && player.getLocation().distance(getServer().getPlayer(runner).getLocation()) < 5) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 0));
                            }


                            runnerworld = String.valueOf(getServer().getPlayer(runner).getLocation().getWorld());

                        }

                        if (sec == 0 && tick == 0) {
                            loc[loctop] = getServer().getPlayer(runner).getLocation();
                            loctop++;
                        }
                        totaltick++;
                        tick++;
                        if (tick == 20) {
                            tick = 0;
                            sec++;
                            if (compass != 0) {
                                compass--;
                            }
                            if (cooltime != 0) {
                                cooltime= cooltime - 1;
                            }



                        }
                        if (sec == 60) {
                            sec = 0;
                            min++;
                        }
                    }
                    catch (Exception e)
                    {
                        e.getMessage();
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
    public void config() {
        consol.sendMessage( ChatColor.AQUA + "[manhunt] config파일 불러오는중");
        consol.sendMessage( ChatColor.AQUA + "[manhunt]");
        saveConfig();
        File cfile;
        try
        {
            cfile = new File(getDataFolder(), "config.yml");
        }
        catch (Exception exception)
        {
            getConfig().options().copyDefaults(true);
            saveConfig();
            cfile = new File(getDataFolder(), "config.yml");
        }

        if (cfile.length() == 0)
        {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }

        skills= Arrays.asList(getConfig().getString("능력들").split(","));
        for(int i = 0;i<skills.size();i++)
        {
            consol.sendMessage( ChatColor.AQUA + "[manhunt]   -"+skills.get(i));
        }


        consol.sendMessage( ChatColor.AQUA + "[manhunt]");
        consol.sendMessage( ChatColor.GREEN + "[manhunt] config파일 불러옴");
    }
    public void manhuntscoreboard(Player player) {
        ScoreboardManager sm = Bukkit.getScoreboardManager();
        board = sm.getNewScoreboard();
        obj = board.registerNewObjective("totaltime", "dummy");
        obj.setDisplayName(ChatColor.YELLOW +"MANHUNT");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        one = obj.getScore(ChatColor.WHITE+"총 플레이 시간");
        one.setScore(rawcnt);


        one = obj.getScore(ChatColor.YELLOW+String.valueOf(min)+"분:"+String.valueOf(sec)+"초");
        one.setScore(rawcnt-1);


        one = obj.getScore(ChatColor.WHITE+"러너가 있는 월드");
        one.setScore(rawcnt-2);

        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world}"))
            one = obj.getScore(ChatColor.GREEN+"overworld");

        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world_nether}"))
            one = obj.getScore(ChatColor.RED+"netherworld");

        if(runnerworld.equalsIgnoreCase("CraftWorld{name=world_the_end}"))
            one = obj.getScore(ChatColor.AQUA+"enderworld");

        one.setScore(rawcnt-3);
        one = obj.getScore(ChatColor.YELLOW+"러너");
        one.setScore(rawcnt-4);
        one = obj.getScore(ChatColor.WHITE+runner);
        one.setScore(rawcnt-5);
        UUID id = player.getUniqueId();

        player.setScoreboard(board);
    }
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, String[] args) {
        if(command.getName().equalsIgnoreCase("des"))
        {
            sender.sendMessage("플러그인 설명");
            sender.sendMessage("/run을 통해 자신을 러너로 설정할 수 있습니다.");
            sender.sendMessage("게임 시작시 러너가 헌터를 때리면 시작");
            if(sender.isOp())
            {
                sender.sendMessage("/manhunt를 통해 게임 시작.");
            }
        }
        if(command.getName().equalsIgnoreCase("run")) {
            if(runner.equalsIgnoreCase(sender.getName()))
            {
                sender.sendMessage("이미 러너입니다.");

            }
            else
            {
                runner = sender.getName();

                getServer().sendMessage(Component.text(runner+"님이 러너가 되셨습니다."));
            }

        }
        if(command.getName().equalsIgnoreCase("go")) {
            Player pl = (Player) sender;
            go(pl.getUniqueId());

        }
        if(command.getName().equalsIgnoreCase("ab")||command.getName().equalsIgnoreCase("ability")) {
            Player pl = (Player) sender;
            openInventory(pl);

        }
        if(command.getName().equalsIgnoreCase("manhunt"))
        {

            if(sender.isOp())
            {
                config();
                isready = true;
                consol.sendMessage(ChatColor.GREEN +"[MANHUNT] 게임 시작!");
                consol.sendMessage(ChatColor.AQUA+"[MANHUNT] 러너");
                consol.sendMessage(ChatColor.AQUA+"[MANHUNT] 　·"+runner);
                consol.sendMessage(ChatColor.YELLOW+"[MANHUNT] 헌터");
                players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                for(int i = 0;i<players.size();i++)
                {
                    Player player = (Player)players.get(i);
                    player.setGameMode(GameMode.ADVENTURE);
                    isparty.put(player.getUniqueId(),true);
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,100));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,100,100));
                    if(player.getName().equalsIgnoreCase(runner))//러너
                    {
                        run = player;
                        openInventory(player);
                        player.getInventory().clear();
                        player.getInventory().addItem(new ItemStack(Material.COMPASS));
                        player.sendTitle(ChatColor.RED+"당신은 러너입니다",ChatColor.GREEN+"헌터를 때리면 시작",40,120,40);
                        skill = skills.get(0);
                        player.sendMessage(ChatColor.WHITE+"당신의 능력은 "+ChatColor.AQUA+skill+ChatColor.WHITE+"입니다.");
                        player.sendMessage("나침반을 우클릭하여 능력을 바꿀 수 있습니다.");
                    }
                    else
                    {
                        player.getInventory().clear();
                        consol.sendMessage(ChatColor.YELLOW+"[MANHUNT] 　·"+player.getName());
                        player.sendTitle(ChatColor.YELLOW+"당신은 헌터입니다",ChatColor.GREEN+"ready!",40,120,40);
                    }
                }

            }
        }
        return true;
    }
    @EventHandler
    public void EnderDragonDeath(EntityDeathEvent e) {
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
    public void RunnerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(ChatColor.RED+"사람이 죽었다.");
        if(e.getEntity().getPlayer().getName().equalsIgnoreCase(runner)&&isgaming&&!isrebirthable)
        {
            isgaming = false;
            e.setDeathSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
            //e.getEntity().getPlayer().setGameMode(GameMode.SPECTATOR);

            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++)
            {
                Player player = (Player) players.get(i);
                player.sendTitle(ChatColor.RED + "러너가 죽었다...", ChatColor.BLUE + "헌터 승!");
            }
        }
        else if(e.getEntity().getPlayer().getName().equalsIgnoreCase(runner)&&isgaming&&isrebirthable)
        {
            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++)
            {
                Player player = (Player) players.get(i);
                player.sendTitle(ChatColor.RED + "러너가 능력을 사용해 되돌아갑니다.", ChatColor.BLUE + "Re:제로");
            }

            e.setCancelled(true);
            e.getEntity().getPlayer().setGameMode(GameMode.SURVIVAL);

            e.getEntity().getPlayer().setHealth(20);
            isrebirthable = false;
            try
            {
                e.getEntity().getPlayer().getInventory().remove(new ItemStack(Material.BLAZE_ROD,1));
                e.getEntity().getPlayer().teleport(loc[0]);
            }
            catch (Exception exception)
            {
                exception.getMessage().length();
                if(loctop>10)
                {
                    e.getEntity().getPlayer().teleport(loc[loctop-10]);
                }

            }
        }
    }










    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().sendMessage("/des 로 설명을 볼 수 있습니다.");
        if(!quitcnt.containsKey(e.getPlayer().getUniqueId()))
        {
            quitcnt.put(e.getPlayer().getUniqueId(),0);
        }
        if (quitplayer.contains(e.getPlayer().getUniqueId())) {/////////////////////////////
            quitplayer.remove(e.getPlayer().getUniqueId());
        }
        if(deadplayer.contains(e.getPlayer().getUniqueId()))
        {
            if(e.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId()))
            {
                deadplayer.remove(e.getPlayer().getUniqueId());
                e.getPlayer().setHealth(0);
            }

        }


        if(warnplayer.contains(e.getPlayer().getUniqueId()))
        {
            if(e.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId()))
            {
                warnplayer.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendTitle("  ", "한번 더 나가시면 3일밴입니다.",0,120,40);
            }

        }

        if (isgaming||isready) {
            try
            {
                if (isparty.get(e.getPlayer().getUniqueId()))
                {
                    e.getPlayer().setGameMode(GameMode.SURVIVAL);
                }
                else
                {
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            }
            catch (Exception exception)
            {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }

        }
        else
            {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
        }

    }
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isparty.get(e.getPlayer().getUniqueId())&&isgaming) {
            e.setQuitMessage(ChatColor.RED + "게임에 참여중인 사람이 나갔습니다. - " + e.getPlayer().getName() + "\n" + "5분 내에 들어오지 않으면 게임에서 추방됩니다.");

            /////////////////////////////////////
            quitcnt.put(e.getPlayer().getUniqueId(),quitcnt.get(e.getPlayer().getUniqueId())+1);
            if(quitcnt.get(e.getPlayer().getUniqueId())==4)
            {
                getServer().sendMessage(Component.text("4번 게임에서 나갔기때문에 강제 추방되었습니다."));
                isparty.put(e.getPlayer().getUniqueId(),false);
                if(e.getPlayer().getName().equalsIgnoreCase(runner))
                {
                    isgaming=false;
                    players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                    for (int i = 0; i < players.size(); i++) {
                        Player player = (Player) players.get(i);
                        player.sendTitle("게임 종료","러너의 트롤",20,60,20);

                    }
                    e.getPlayer().banPlayer("게임중 퇴장",Date.from(Instant.now().plusSeconds(60*60*24*3)));
                    getServer().sendMessage(Component.text(ChatColor.RED+"러너가 추방되어 게임이 종료되었습니다...\n러너 3일 밴"));
                }

                deadplayer.add(e.getPlayer().getUniqueId());
            }
            if(quitcnt.get(e.getPlayer().getUniqueId())==3)
            {
                warnplayer.add(e.getPlayer().getUniqueId());
            }
            quitplayer.add(e.getPlayer().getUniqueId());
            new BukkitRunnable() {

                @Override
                public void run() {

                    if (!quitplayer.contains(e.getPlayer().getUniqueId())) {
                        return;
                    }
                    consol.sendMessage("ww");
                    getServer().sendMessage(Component.text(ChatColor.RED+"5분이 지났습니다.\n게임에서 추방되었습니다"));
                    isparty.put(e.getPlayer().getUniqueId(),false);
                    if(e.getPlayer().getName().equalsIgnoreCase(runner))
                    {
                        isgaming=false;
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            player.sendTitle("게임 종료","러너의 트롤",20,60,20);
                        }
                        e.getPlayer().banPlayer("게임중 퇴장",Date.from(Instant.now().plusSeconds(60*60*24)));
                        getServer().sendMessage(Component.text(ChatColor.RED+"러너가 추방되어 게임이 종료되었습니다...\n러너 1일 밴"));
                    }
                    deadplayer.add(e.getPlayer().getUniqueId());
                }

            }.runTaskLater(this,20*60*5);          //5분
        }
    }
    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player)
        {
            Player whoHit = (Player) e.getDamager();
            UUID id = whoHit.getUniqueId();
            if(whoHit.getPlayer().getName().equalsIgnoreCase(runner))
            {
                go(id);
            }
            else if(isready)
            {
                e.setCancelled(true);
            }

        }
    }
    public void go(UUID id) {
        id = getServer().getPlayer(runner).getUniqueId();
        if(isready)
        {
            isready=false;
            isgaming = true;
            cooltime=0;
            players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for(int i = 0;i<players.size();i++)
            {

                Player player = (Player)players.get(i);
                player.setGameMode(GameMode.SURVIVAL);
                player.setMaxHealth(20);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                if(player.getName().equalsIgnoreCase(runner))//러너
                {
                    player.sendMessage("당신의 능력은 "+skill+"입니다.");
                    player.setMaxHealth(maxhealth);

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




    @EventHandler
    public void PlayerClickBlock(PlayerInteractEvent e) {

        Player p =e.getPlayer();
        if((e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))&&p.getInventory().getItemInMainHand().getType() == Material.COMPASS)
        {
            if(e.getPlayer().getName().equalsIgnoreCase(runner)&&isready)
            {
                openInventory(e.getPlayer());
            }
        }

        if(!isgaming)
        {
            e.setCancelled(true);
        }
        if(e.getAction().equals(Action.LEFT_CLICK_AIR)||e.getAction().equals(Action.LEFT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK)&&itemclick)
        {
            try
            {
                ItemStack firstitem = p.getInventory().getItemInMainHand().asOne();
                if (cooltime!= 0)
                {

                    p.sendActionBar(ChatColor.RED + "스킬을 사용할 수 없습니다.");
                    return;
                }
                if (p.getInventory().getItemInMainHand().getType() == item)
                {
                        cooltime= cool;
                        p.getInventory().removeItem(firstitem);
                        p.sendTitle("  ","능력 사용!",20,40,20);
                        compass=compasscool;
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++)
                        {
                            Player player = (Player) players.get(i);
                            if (!player.getName().equalsIgnoreCase(runner))//러너
                            {
                                p.sendMessage(ChatColor.AQUA+"러너가 능력을 사용하였습니다.");
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }

                }


            }
            catch (Exception exception)
            {
                int a = 5;
            }
        }


    }






    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
    }
    






    public Hypixel() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "ability");

        // Put the items into the inventory

    }
    public void initializeItems() {
        inv.clear();
        for(int i = 0;i< skills.size();i++)
        {
            if(skills.get(i)==skill)
            {
                inv.addItem(createGuiItem(Material.ENCHANTED_BOOK,ChatColor.RED+skills.get(i),ChatColor.GREEN+"[Selected]"));
            }
            else

            {
                inv.addItem(createGuiItem(Material.ENCHANTED_BOOK,ChatColor.RED+skills.get(i),ChatColor.YELLOW+"[Select to click]"));
            }
        }
        inv.addItem(createGuiItem(Material.BARRIER,"닫기",""));
    }
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }
    public void openInventory(final HumanEntity ent) {
        if(isready&&ent.getName().equalsIgnoreCase(runner))
        {
            initializeItems();
            ent.openInventory(inv);
        }

    }
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();


        if(e.getRawSlot()<skills.size())
        {
            p.sendMessage("당신은 "+skills.get(e.getRawSlot())+" 능력을 선택하였습니다.");
            skill = skills.get(e.getRawSlot());
        }
        else if(e.getCurrentItem().getType()==Material.BARRIER)
        {
            p.closeInventory();
        }

        initializeItems();
    }
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv))
        {
            e.setCancelled(true);
        }
    }
}
