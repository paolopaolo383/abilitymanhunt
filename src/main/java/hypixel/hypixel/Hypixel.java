package hypixel.hypixel;

import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;

import java.io.File;
import java.util.*;

public final class Hypixel extends JavaPlugin implements Listener
{

    enum skills {superinvisible}
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
    HashMap<UUID, skills> skill = new HashMap<UUID, skills>();
    @SuppressWarnings("deprecation")
    @Override
    public void onEnable()
    {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage( ChatColor.AQUA + "[Hypixel] 하이픽셀 플러그인 v1.1 활성화.");
        consol.sendMessage( ChatColor.AQUA + "[Hypixel] config파일 불러오는중");
        saveConfig();
        File cfile = new File(getDataFolder(), "config.yml");
        if (cfile.length() == 0)
        {
            getConfig().options().copyDefaults(true);
            saveConfig();
        }
        consol.sendMessage( ChatColor.GREEN + "[Hypixel] config파일 불러옴");
        consol.sendMessage( ChatColor.AQUA + "[Hypixel] 레시피 제작중");



        ItemStack item = new ItemStack(Material.IRON_PICKAXE);
        item.addEnchantment(Enchantment.DIG_SPEED,1);
        item.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        ShapedRecipe newrecipe = new ShapedRecipe(new NamespacedKey(this, "quick_pick"),item).shape(new String[]{"@@@","P#P"," # "}).setIngredient('@',Material.IRON_ORE).setIngredient('#',Material.STICK).setIngredient('P',Material.COAL);
        getServer().addRecipe(newrecipe);


        item = new ItemStack(Material.IRON_INGOT,10);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "irons"),item).shape(new String[]{"@@@","@#@","@@@"}).setIngredient('@',Material.IRON_ORE).setIngredient('#',Material.COAL);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.GOLD_INGOT,10);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "golds"),item).shape(new String[]{"@@@","@#@","@@@"}).setIngredient('@',Material.GOLD_ORE).setIngredient('#',Material.COAL);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.IRON_SWORD,1);
        item.addEnchantment(Enchantment.DAMAGE_ALL,2);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "special_sward"),item).shape(new String[]{" @ "," # "," @ "}).setIngredient('@',Material.REDSTONE_BLOCK).setIngredient('#',Material.IRON_SWORD);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.BOW,1);
        item.addEnchantment(Enchantment.ARROW_DAMAGE,2);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "special_bow"),item).shape(new String[]{" @#","@ #"," @#"}).setIngredient('@',Material.REDSTONE_TORCH).setIngredient('#',Material.STRING);
        getServer().addRecipe(newrecipe);


        item = new ItemStack(Material.ENCHANTED_BOOK,1);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "protect_book"),item).shape(new String[]{"   "," @@"," @#"}).setIngredient('@',Material.PAPER).setIngredient('#',Material.IRON_INGOT);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.ENCHANTED_BOOK,1);
        item.addUnsafeEnchantment(Enchantment.DAMAGE_ALL,1);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "sharp_book"),item).shape(new String[]{"Q  "," @@"," @#"}).setIngredient('@',Material.PAPER).setIngredient('Q',Material.FLINT).setIngredient('#',Material.IRON_SWORD);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.IRON_HELMET,1);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL,1);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_PROJECTILE,1);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_EXPLOSIONS,1);
        item.addUnsafeEnchantment(Enchantment.PROTECTION_FIRE,1);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "special_helmat"),item).shape(new String[]{"@@@","@#@","   "}).setIngredient('@',Material.IRON_INGOT).setIngredient('#',Material.REDSTONE_TORCH);
        getServer().addRecipe(newrecipe);

        item = new ItemStack(Material.DIAMOND_PICKAXE,1,(short) 1544);
        item.addUnsafeEnchantment(Enchantment.LOOT_BONUS_BLOCKS,1);
        item.addUnsafeEnchantment(Enchantment.DURABILITY,1);
        newrecipe = new ShapedRecipe(new NamespacedKey(this, "luck"),item).shape(new String[]{"$@$","Q#Q"," # "}).setIngredient('@',Material.GOLD_ORE).setIngredient('$',Material.IRON_ORE).setIngredient('Q',Material.LAPIS_BLOCK).setIngredient('#',Material.STICK);
        getServer().addRecipe(newrecipe);

        consol.sendMessage( ChatColor.GREEN + "[Hypixel] 레시피 제작완료");

        //item = new ItemStack(Material.COAL,1);

        //newrecipe = new ShapedRecipe(item).shape(new String[]{"@@@","@@@","@@@"}).setIngredient('@',Material.PLANK);
        //getServer().addRecipe(newrecipe);
        new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if(isgaming)
                {
                    List<Player> players = Objects.requireNonNull(getServer().getWorld("uhc")).getPlayers();
                    for (Player player : players) {
                        Player pl = player;
                        uhcscboard(pl);

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
                        sec=0;
                        min++;
                    }


                    if(min==10&&sec==0&&tick==0)
                    {
                        getServer().getWorld("uhc").setPVP(true);
                        isdropinghead = true;
                        getServer().getWorld("uhc").getWorldBorder().setSize(50, 2000);
                        List play = getServer().getWorld("uhc").getPlayers();
                        int q = play.size();
                        for(int i = 0;i<q;i++)
                        {
                            Player pl = (Player) play.get(i);
                            pl.sendTitle(ChatColor.YELLOW+"자기장이 줄어듭니다","평화시간 끝", 0,40,20);
                        }
                    }
                    if(min==45&&sec==0&&tick==0)
                    {
                        List play = Objects.requireNonNull(getServer().getWorld("uhc")).getPlayers();
                        for (Object o : play) {
                            Player pl = (Player) o;
                            pl.sendTitle(ChatColor.RED + "자기장이 줄어듭니다", "독 효과가 부여됩니다", 0, 40, 20);
                            if (pl.getGameMode() == GameMode.SURVIVAL) {
                                pl.setGlowing(true);
                                pl.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 1000000000, 2));
                            }
                        }
                        Objects.requireNonNull(getServer().getWorld("uhc")).getWorldBorder().setSize(5, 180);
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
                    List<Player> players = Objects.requireNonNull(getServer().getWorld("world")).getPlayers();
                    for (Player pl : players) {
                        pl.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    }
                }
            }

        }.runTaskTimer(this, 0L, 1L);
    }

    @EventHandler
    public void playershift(PlayerToggleSneakEvent e)
    {

        if(e.getPlayer().isSneaking()) //이제 안함
        {
            e.getPlayer().sendMessage("하는중");
        }
        else//이제 시작
        {
            e.getPlayer().sendMessage("---------------------");
        }
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

        six = obj.getScore((min>=10?ChatColor.RED:ChatColor.GREEN)+String.valueOf(-1*(int)getServer().getWorld("uhc").getWorldBorder().getSize()/2)+" , "+String.valueOf((int)getServer().getWorld("uhc").getWorldBorder().getSize()/2));
        six.setScore(p);
        p++;

        seven = obj.getScore(ChatColor.WHITE+"자기장 좌표");
        seven.setScore(p);
        p++;

        player.setScoreboard(board);
    }





    @EventHandler
    public void leave(PlayerQuitEvent e)
    {

        e.setQuitMessage(getConfig().getString("서버퇴장"));
        if(e.getPlayer().getGameMode()==GameMode.SURVIVAL)
        {

            if(isgaming)
            {
                e.getPlayer().setHealth(0);
            }
        }
        if(isgaming)
        {
            Player Winner = null;
            int sur = 0;
            List players = getServer().getWorld("uhc").getPlayers();
            int top = players.size();
            for(int i = 0;i<top;i++)
            {
                Player pl = (Player) players.get(i);
                if(pl.getGameMode()==GameMode.SURVIVAL)
                {
                    sur++;
                    Winner = (Player) pl;

                }
            }
            if(sur==1)
            {
                setGame((Player)Winner);
            }
        }

    }
    @EventHandler
    public void dead(PlayerDeathEvent e)
    {
        Player player  = e.getEntity();
        getServer().sendMessage(Component.text(ChatColor.RED+"사람이 죽었다"));
        ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta skullMeta = (SkullMeta) skull.getItemMeta();
        skullMeta.setDisplayName(player.getName()+"'s head");
        skullMeta.setOwner(player.getName()+"'s head");
        skull.setItemMeta(skullMeta);
        skull.addUnsafeEnchantment(Enchantment.LUCK,100);
        e.getEntity().getWorld().dropItemNaturally(e.getEntity().getLocation(),skull);
        if(e.getEntity().getType()==EntityType.PLAYER)
        {
            if(isdropinghead&&isgaming)
            {


                //ItemStack skull = new ItemStack(Material.PLAYER_HEAD, 1, (short) SkullType.PLAYER.ordinal());
                //SkullMeta meta = (SkullMeta) skull.getItemMeta();

                getServer().getWorld("uhc").dropItemNaturally(e.getEntity().getLocation(),skull);




                e.getEntity().setGameMode(GameMode.SPECTATOR);
                Player Winner = null;
                int sur = 0;
                List players = getServer().getWorld("uhc").getPlayers();
                int top = players.size();
                for(int i = 0;i<top;i++)
                {
                    Player pl = (Player) players.get(i);
                    if(pl.getGameMode()==GameMode.SURVIVAL)
                    {
                        sur++;
                        Winner = (Player) pl;
                    }
                }
                if(sur==1)
                {
                    setGame((Player)Winner);
                }
            }

        }


    }
    @EventHandler
    public void join(PlayerJoinEvent e)
    {

        consol.sendMessage( ChatColor.YELLOW + "[사람이 들어옴]");
        Player player = e.getPlayer();
        player.getAttribute(Attribute.GENERIC_ATTACK_SPEED).setBaseValue(1000);
        player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
        UUID uuid = player.getUniqueId();
        diamond.put(uuid,0);
        stone.put(uuid,0);
        hack.put(uuid,0);
        e.setJoinMessage(getConfig().getString("서버접속"));
        player.sendMessage(ChatColor.RED+"서버 소개\n"+ChatColor.WHITE+"공격 딜레이가 없습니다.\n/uhc명령어를 통해 게임을 시작할 수 있습니다.\n특별 레시피가 있습니다.\n레시피는 기본적으로 조합법을 드립니다.");
        if(game=="uhc"&&isgaming&&isdropinghead)
        {
            player.setGameMode(GameMode.SPECTATOR);
        }
        else if(!isgaming)
        {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
            e.getPlayer().teleport(getServer().getWorld("world").getHighestBlockAt(0, 0).getLocation().add(0,1,0));
        }

    }
    public void setGame(Player winner)
    {
        isgaming = false;
        totaltick = 0;
        tick = 0;
        sec = 0;
        min = 0;


        List players = getServer().getWorld("uhc").getPlayers();
        int top = players.size();
        for(int i = 0;i<top;i++)
        {
            Player pl = (Player) players.get(i);
            pl.setGlowing(false);
            pl.resetTitle();
            pl.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
            pl.sendTitle(winner.getName(),ChatColor.YELLOW+"승리!", 0,100,40);
            pl.teleport(getServer().getWorld("uhc").getHighestBlockAt(0, 0).getLocation().add(0,1,0));
            pl.setGameMode(GameMode.SURVIVAL);
        }
        winner.setGlowing(true);

    }
    @EventHandler
    public void playerchat(PlayerChatEvent e)
    {
        e.getPlayer().sendMessage("이 서버에서는 채팅을 칠 수 없습니다.");
        e.setCancelled(true);
    }

    @EventHandler
    public void PlayerClickBlock(PlayerInteractEvent e) {

        Player p =e.getPlayer(); // 플레이어가 액션을 취했을때 플레이어 저장 (Ex: 우클릭, 좌클릭 할때 저장)

        try {
            if(e.getAction().equals(Action.LEFT_CLICK_AIR)||e.getAction().equals(Action.LEFT_CLICK_BLOCK)||e.getAction().equals(Action.RIGHT_CLICK_AIR))
            {
                ItemStack firstitem = p.getInventory().getItemInMainHand().asOne();
                if(p.getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(Enchantment.LUCK)==100)
                {
                    p.removePotionEffect(PotionEffectType.SPEED);
                    p.removePotionEffect(PotionEffectType.REGENERATION);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,100, 1));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,200, 2));
                    p.getInventory().removeItem(firstitem);
                }
                if ( p.getInventory().getItemInMainHand().getType().equals(Material.ZOMBIE_HEAD))
                {
                    firstitem = new ItemStack(Material.ZOMBIE_HEAD,1);
                    p.removePotionEffect(PotionEffectType.SPEED);
                    p.removePotionEffect(PotionEffectType.REGENERATION);
                    p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED,600, 2));
                    p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION,200, 3) );
                    p.getInventory().removeItem(firstitem);
                }
            }
            if(p.getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(Enchantment.LUCK)==100||e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            {
                e.setCancelled(true);
            }
        }
        catch(NullPointerException exception)
        {
            return;
        }

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
            if((rate*100)>6.0&&min>10&&game=="uhc")
            {
                //핵 판정
                e.getPlayer().setHealth(0);
                getServer().getPlayer("481926").sendTitle(e.getPlayer().getName(),"핵인듯");
                e.getPlayer().kick(Component.text("핵 쓰지 마세요!"));

            }
            if((rate*100)>3.0&&min>10&&game=="uhc")
            {
                //핵 판정
                getServer().getPlayer("481926").sendTitle(e.getPlayer().getName(),"핵인듯하다 아직 모름");

            }
        }
        if(e.getBlock().getType()==Material.STONE)
        {
            stone.put(uuid,stone.get(uuid)+1);
            rate = diamond.get(uuid)/(float)stone.get(uuid);
            e.getPlayer().sendTitle(Double.toString((rate*100)),"음");

        }
    }
    @Override
    public void onDisable()
    {
        if (isgaming)
        {

        }
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

        world.setPVP(false);


        world.setDifficulty(Difficulty.EASY);
        //world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
        world.setGameRule(GameRule.NATURAL_REGENERATION,false);
        world.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN,true);

        world.getWorldBorder().setCenter(0,0);
        world.getWorldBorder().setDamageAmount(2);
        world.getWorldBorder().setWarningDistance(100);
        world.getWorldBorder().setDamageBuffer(0);
        world.getWorldBorder().setSize(2000);

        game = "uhc";
        isdropinghead = false;

        List players = getServer().getWorld("world").getPlayers();
        for (int i = 0;i<players.size();i++)
        {
            Player pl = (Player) players.get(i);
            pl.setGlowing(false);
            Random createRandom = new Random();
            int xi = createRandom.nextInt(1000)-500;
            int zi = createRandom.nextInt(1000)-500;
            pl.teleport(world.getHighestBlockAt(xi, zi).getLocation().add(0, 1, 0));

            pl.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(40);
            pl.setGameMode(GameMode.SURVIVAL);

            pl.setCompassTarget(new Location(getServer().getWorld("uhc"),0,50,0));
            pl.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 300, 255, true));
            pl.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 12000, 255, true));

            pl.getInventory().clear();
            ItemStack firstitem = new ItemStack(Material.STONE_PICKAXE, 1);
            pl.getInventory().addItem(firstitem);

            firstitem = new ItemStack(Material.STONE_AXE, 1);
            pl.getInventory().addItem(firstitem);
            firstitem = new ItemStack(Material.STONE_SWORD, 1);
            firstitem.addUnsafeEnchantment(Enchantment.FIRE_ASPECT, 1);
            pl.getInventory().addItem(firstitem);
            firstitem = new ItemStack(Material.STONE_SHOVEL, 1);
            pl.getInventory().addItem(firstitem);

        }

        isgaming = true;

    }


}
