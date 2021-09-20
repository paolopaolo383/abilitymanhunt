package hypixel.hypixel;

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
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public final class Hypixel extends JavaPlugin implements Listener, CommandExecutor
{
    enum skills
    {
        stealth,
        covid19
    }
    private Inventory inv;
    private String runner = "__481926__";
    private static final int rawcnt = 4;
    private String runnerworld="OverWorld";
    private Scoreboard board;
    private Objective obj;
    private Score one;
    private Player run;
    boolean isready = false;
    boolean isgaming=false;
    int totaltick=0;
    int min, sec, tick;
    List players;
    int compass=0;
    HashMap<UUID, skills> skill = new HashMap<UUID, skills>();
    HashMap<UUID, Integer> cooltime = new HashMap<UUID, Integer>();
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
                        UUID id = player.getUniqueId();
                        manhuntscoreboard(player);//스코어 보드
                        if(compass==0)
                        {
                            player.setCompassTarget(getServer().getPlayer(runner).getLocation());
                        }
                        if(tick==19&&!player.getName().equalsIgnoreCase(runner)&&player.getLocation().distance(getServer().getPlayer(runner).getLocation())<10)
                        {
                            player.addPotionEffect(new PotionEffect(PotionEffectType.POISON,20,1));
                        }



                        runnerworld=String.valueOf(getServer().getPlayer(runner).getLocation().getWorld());
                    }


                    totaltick++;
                    tick++;
                    if (tick==20)
                    {
                        tick=0;
                        sec++;
                        if(compass!=0)
                        {
                            compass--;
                        }
                        if(cooltime.get(getServer().getPlayer(runner).getUniqueId())!=0)
                        {
                            cooltime.put(getServer().getPlayer(runner).getUniqueId(),cooltime.get(getServer().getPlayer(runner).getUniqueId())-1);
                        }


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
        if(player.getName().equalsIgnoreCase(runner))
        {
            a = -2;
        }

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
        UUID id = player.getUniqueId();

        if(player.getName().equalsIgnoreCase(runner))
        {
            if(skills.stealth==skill.get(id)&&cooltime.get(id)!=0)
            {
                one = obj.getScore(ChatColor.WHITE+"스킬 쿨타임");
                one.setScore(rawcnt-a);
                a++;

                one = obj.getScore(ChatColor.RED+String.valueOf(cooltime.get(id))+"초");
                one.setScore(rawcnt-a);
                a++;
            }
            else
            {
                one = obj.getScore(ChatColor.RED+"스킬 사용 가능");
                one.setScore(rawcnt-a);
                a++;
                one = obj.getScore(ChatColor.WHITE+"철괴 또는 다이아몬드 우클릭");
                one.setScore(rawcnt-a);
                a++;
            }
        }

        player.setScoreboard(board);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, String[] args)
    {
        if(command.getName().equalsIgnoreCase("ran")) {
            runner = sender.getName();

        }
        if(command.getName().equalsIgnoreCase("ab")||command.getName().equalsIgnoreCase("ability")) {
            Player pl = (Player) sender;
            openInventory(pl);

        }
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
                player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,100,100));
                player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION,100,100));
                if(player.getName().equalsIgnoreCase(runner))//러너
                {
                    run = player;
                    skill.put(player.getUniqueId(),skills.stealth);
                    openInventory(player);
                    player.getInventory().clear();
                    player.getInventory().addItem(new ItemStack(Material.COMPASS));
                    player.sendTitle(ChatColor.RED+"당신은 러너입니다",ChatColor.GREEN+"헌터를 때리면 시작",40,120,40);
                    player.sendMessage("당신의 능력은 은둔자입니다. \n나침반을 우클릭하여 능력을 바꿀 수 있습니다.");
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
            UUID id = whoHit.getUniqueId();
            if(whoHit.getPlayer().getName().equalsIgnoreCase(runner))
            {
                if(isready)
                {
                    isready=false;
                    isgaming = true;
                    cooltime.put(id,0);
                    skill.put(id,skills.stealth);
                    players=Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                    for(int i = 0;i<players.size();i++)
                    {
                        Player player = (Player)players.get(i);
                        if(player.getName().equalsIgnoreCase(runner))//러너
                        {
                            if(skill.get(id)==skills.stealth)
                            {
                                player.sendMessage("당신의 능력은 은둔자입니다");
                            }
                            if(skill.get(id)==skills.covid19)
                            {
                                player.sendMessage("당신의 능력은 확진자입니다");
                                player.setMaxHealth(16);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE , 100000000 , 1));
                            }

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
            else if(isready)
            {
                e.setCancelled(true);
            }

        }
    }





    @EventHandler
    public void PlayerClickBlock(PlayerInteractEvent e)
    {

        Player p =e.getPlayer(); // 플레이어가 액션을 취했을때 플레이어 저장 (Ex: 우클릭, 좌클릭 할때 저장)

        if(e.getAction().equals(Action.LEFT_CLICK_AIR)||e.getAction().equals(Action.LEFT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
        {
            try
            {
                ItemStack firstitem = p.getInventory().getItemInMainHand().asOne();
                if (p.getInventory().getItemInMainHand().getType() == Material.IRON_INGOT) {
                    if (skills.stealth == skill.get(p.getUniqueId()) && cooltime.get(p.getUniqueId()) != 0) {

                        p.sendActionBar(ChatColor.RED + "스킬을 사용할 수 없습니다.");

                    }
                    if (skills.stealth == skill.get(p.getUniqueId()) && cooltime.get(p.getUniqueId()) == 0)//은신 기술&&쿨 0
                    {
                        cooltime.put(p.getUniqueId(), 70);
                        p.getInventory().removeItem(firstitem);
                        p.sendTitle("  ","능력 사용!",20,40,20);
                        compass=15;
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            if (!player.getName().equalsIgnoreCase(runner))//러너
                            {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND) {
                    if (skills.stealth == skill.get(p.getUniqueId()) && cooltime.get(p.getUniqueId()) != 0) {
                        p.sendActionBar(ChatColor.RED + "스킬을 사용할 수 없습니다.");
                    }
                    if (skills.stealth == skill.get(p.getUniqueId()) && cooltime.get(p.getUniqueId()) == 0)//은신 기술&&쿨 0
                    {
                        p.sendTitle("  ","능력 사용!",20,40,20);
                        cooltime.put(p.getUniqueId(), 120);
                        compass=15;
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                        p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
                        p.getInventory().removeItem(firstitem);


                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            if (!player.getName().equalsIgnoreCase(runner))//러너
                            {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 300, 1));

                                player.removePotionEffect(PotionEffectType.BLINDNESS);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 300, 0));

                                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 300, 1));
                            }
                        }
                    }
                }
            }
            catch (Exception exception)
            {
                int a = 5;
            }
        }

        if((e.getAction().equals(Action.RIGHT_CLICK_AIR)||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))&&p.getInventory().getItemInMainHand().getType() == Material.COMPASS)
        {
            if(e.getPlayer().getName().equalsIgnoreCase(runner))
            {
                openInventory(e.getPlayer());
            }
        }
    }










    public Hypixel() {
        // Create a new inventory, with no owner (as this isn't a real inventory), a size of nine, called example
        inv = Bukkit.createInventory(null, 9, "ability");

        // Put the items into the inventory

    }

    // You can call this whenever you want to put the items in
    public void initializeItems()
    {
        inv.clear();
        if(skill.get(getServer().getPlayer(runner).getUniqueId())==skills.stealth)
        {
            inv.addItem(createGuiItem(Material.IRON_INGOT, 
                    ChatColor.RED+"은둔자", ChatColor.GREEN+"철괴", ChatColor.WHITE+"클릭하여 헌터의 나침반을 무력화시키고",ChatColor.WHITE+"구속1을 부여한다.","  ",ChatColor.GREEN+"다이아몬드",ChatColor.WHITE+"클릭하여 헌터에게 구속과 실명과 힘을 부여한다",ChatColor.WHITE+"자신에게는 신속과 저항이 부여된다.","  ",ChatColor.GREEN+"[Selected]"));
        }
        else
        {
            inv.addItem(createGuiItem(Material.IRON_INGOT, ChatColor.RED + "은둔자", ChatColor.GREEN + "철괴", ChatColor.WHITE + "클릭하여 헌터의 나침반을 무력화시키고", ChatColor.WHITE + "구속1을 부여한다.", "  ", ChatColor.GREEN + "다이아몬드", ChatColor.WHITE + "클릭하여 헌터에게 구속과 실명과 힘을 부여한다", ChatColor.WHITE + "자신에게는 신속과 저항이 부여된다.","  ",ChatColor.YELLOW+"[Select to click]"));
        }
        if(skill.get(getServer().getPlayer(runner).getUniqueId())==skills.covid19)
        {
            inv.addItem(createGuiItem(Material.GOLD_INGOT, ChatColor.RED+"확진자", ChatColor.GREEN+"거리두기", ChatColor.WHITE+"적과 10칸 이내로 가까이있다면",ChatColor.WHITE+"적에게 독을 부여합니다",ChatColor.GREEN+"패시브","힘 2를 받습니다,체력이 8칸입니다","  ",ChatColor.GREEN+"[Selected]"));
        }
        else
        {
            inv.addItem(createGuiItem(Material.GOLD_INGOT, ChatColor.RED+"확진자", ChatColor.GREEN+"거리두기", ChatColor.WHITE+"적과 10칸 이내로 가까이있다면",ChatColor.WHITE+"적에게 독을 부여합니다",ChatColor.GREEN+"패시브","힘 2를 받습니다,체력이 8칸입니다","  ",ChatColor.YELLOW+"[Select to click]"));
        }
        inv.addItem(createGuiItem(Material.RED_STAINED_GLASS_PANE,"  ","  "));//covid19
        inv.addItem(createGuiItem(Material.YELLOW_STAINED_GLASS_PANE,"  ","  "));
        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE,"  ","  "));
        inv.addItem(createGuiItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"  ","  "));
        inv.addItem(createGuiItem(Material.BLUE_STAINED_GLASS_PANE,"  ","  "));
        inv.addItem(createGuiItem(Material.PURPLE_STAINED_GLASS_PANE,"  ","  "));
        inv.addItem(createGuiItem(Material.BARRIER,"닫기","  "));
    }

    // Nice little method to create a gui item with a custom name, and description
    protected ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        // Set the name of the item
        meta.setDisplayName(name);

        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    // You can open the inventory with this
    public void openInventory(final HumanEntity ent)
    {
        if(isready&&ent.getName().equalsIgnoreCase(runner))
        {
            initializeItems();
            ent.openInventory(inv);
        }

    }

    // Check for clicks on items
    @EventHandler
    public void onInventoryClick(final InventoryClickEvent e) {
        if (e.getInventory() != inv) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType().isAir()) return;

        final Player p = (Player) e.getWhoClicked();


        if(e.getRawSlot()==0)
        {
            p.sendMessage("당신은 은둔자 능력을 선택하였습니다.");
            skill.put(p.getUniqueId(),skills.stealth);
        }
        if(e.getRawSlot()==1)
        {
            p.sendMessage("당신은 코로나 확진자 능력을 선택하였습니다.");
            skill.put(p.getUniqueId(),skills.covid19);
        }



        if(e.getRawSlot()==8)
        {
            p.closeInventory();
        }
        initializeItems();
    }

    // Cancel dragging in our inventory
    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
