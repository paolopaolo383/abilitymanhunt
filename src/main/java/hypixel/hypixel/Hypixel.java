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
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
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
import org.bukkit.inventory.meta.CompassMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.jetbrains.annotations.NotNull;

import java.time.Instant;
import java.util.*;

public final class Hypixel extends JavaPlugin implements Listener, CommandExecutor {
    enum skills {
        stealth,
        covid19,
        prophet,
        bomber,
        wolf
    }

    private final Inventory inv;
    private String runner = "__481926__";
    private static final int rawcnt = 6;
    private String runnerworld = "OverWorld";
    private Scoreboard board;
    private Objective obj;
    private int top = 0;
    private boolean isrebirthable = false;
    private Score one;
    private Player run;
    boolean isready = false;
    boolean isgaming = false;
    int totaltick = 0;
    int min, sec, tick;
    private List players;
    int compass = 0;


    List<UUID> deadplayer = new ArrayList<UUID>();
    List<UUID> warnplayer = new ArrayList<UUID>();
    org.bukkit.Location loc[] = new org.bukkit.Location[500];
    private static List<UUID> quitplayer = new ArrayList<UUID>();
    skills skill = skills.covid19;
    HashMap<UUID, Integer> quitcooltime = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> quitcnt = new HashMap<UUID, Integer>();
    HashMap<UUID, Integer> cooltime = new HashMap<UUID, Integer>();
    ConsoleCommandSender consol = Bukkit.getConsoleSender();
    HashMap<UUID, Boolean> isparty = new HashMap<UUID, Boolean>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        consol.sendMessage(ChatColor.AQUA + "[manhunt] ???????????? ?????????.");


        new BukkitRunnable() {
            @Override
            public void run() {
                if (isgaming) {
                    if (getServer().getPlayer(runner) != null) {
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            UUID id = player.getUniqueId();
                            manhuntscoreboard(player);//????????? ??????

                            if (compass == 0) {
                                if (player.getInventory().getItemInMainHand() != null) {
                                    if (player.getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {
                                        ItemMeta meta = player.getInventory().getItemInMainHand().getItemMeta();


                                        CompassMeta compassm = (CompassMeta) meta;
                                        compassm.setLodestone(getServer().getPlayer(runner).getLocation());
                                        if (player.getWorld().equals(getServer().getPlayer(runner))) {
                                            compassm.setLodestoneTracked(true);
                                        } else {
                                            compassm.setLodestoneTracked(false);
                                        }
                                        player.getInventory().getItemInMainHand().setItemMeta(compassm);
                                    }
                                }
                            }

                            if (skill == skills.covid19 && tick == 19 && !player.getName().equalsIgnoreCase(runner) && player.getLocation().distance(getServer().getPlayer(runner).getLocation()) < 5) {
                                player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 30, 0));
                            }


                            runnerworld = String.valueOf(getServer().getPlayer(runner).getLocation().getWorld());

                        }
                        if (sec == 0 && tick == 0) {
                            loc[top] = getServer().getPlayer(runner).getLocation();
                            top++;
                        }
                        totaltick++;
                        tick++;
                        if (tick == 20) {
                            tick = 0;
                            sec++;
                            if (compass != 0) {
                                compass--;
                            }
                            if (cooltime.get(getServer().getPlayer(runner).getUniqueId()) != 0) {
                                cooltime.put(getServer().getPlayer(runner).getUniqueId(), cooltime.get(getServer().getPlayer(runner).getUniqueId()) - 1);
                            }
                            if (skills.wolf == skill && getServer().getPlayer(runner).getWorld().getEnvironment() == World.Environment.NORMAL && !getServer().getWorld("world").isDayTime()) {
                                getServer().getPlayer(runner).getWorld().spawnParticle(Particle.SPELL_WITCH, getServer().getPlayer(runner).getLocation(), 5, 1.2F, 0F, 1.2F);
                                getServer().getPlayer(runner).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 21, 0));
                                getServer().getPlayer(runner).addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 21, 1));
                                getServer().getPlayer(runner).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 21, 1));
                            }


                        }
                        if (sec == 60) {
                            sec = 0;
                            min++;
                            if (min % 30 == 1) {
                                if (skill == skills.wolf) {
                                    getServer().getPlayer(runner).getWorld().spawnParticle(Particle.SPELL_WITCH, getServer().getPlayer(runner).getLocation(), 40, 1.2F, 0F, 1.2F);
                                    getServer().getPlayer(runner).sendTitle("  ", "?????? ??????", 20, 40, 20);
                                    Wolf wolf = (Wolf) getServer().getWorld("world").spawnEntity(getServer().getPlayer(runner).getLocation(), EntityType.WOLF);

                                    wolf.setOwner(getServer().getPlayer(runner));
                                    wolf = (Wolf) getServer().getWorld("world").spawnEntity(getServer().getPlayer(runner).getLocation(), EntityType.WOLF);

                                    wolf.setOwner(getServer().getPlayer(runner));
                                    wolf = (Wolf) getServer().getWorld("world").spawnEntity(getServer().getPlayer(runner).getLocation(), EntityType.WOLF);

                                    wolf.setOwner(getServer().getPlayer(runner));
                                }

                            }
                        }
                    } else //
                    {

                    }
                } else {

                    totaltick = 0;
                    tick = 0;
                    sec = 0;
                    min = 0;

                }
            }

        }.runTaskTimer(this, 0L, 1L);
    }

    public void manhuntscoreboard(Player player) {
        int a = rawcnt;
        if (player.getName().equalsIgnoreCase(runner) && (skill == skills.stealth || skill == skills.prophet)) {
            a = a + 2;
        }

        ScoreboardManager sm = Bukkit.getScoreboardManager();
        board = sm.getNewScoreboard();
        obj = board.registerNewObjective("totaltime", "dummy");
        obj. setDisplayName(ChatColor.YELLOW + "MANHUNT");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);

        one = obj.getScore(ChatColor.WHITE + "??? ????????? ??????");
        one.setScore(a);
        String txt = "";
        if (min / 60 > 0) {
            txt += min / 60 + "??????:";
        }

        one = obj.getScore(ChatColor.YELLOW + txt + String.valueOf(min) + "???:" + String.valueOf(sec) + "???");
        a++;
        one.setScore(a - 2);


        one = obj.getScore(ChatColor.YELLOW + "??????");
        one.setScore(a - 3);
        one = obj.getScore(ChatColor.WHITE + runner);
        one.setScore(a - 4);
        UUID id = player.getUniqueId();

        if (player.getName().equalsIgnoreCase(runner) && (skill == skills.stealth || skill == skills.prophet)) {
            if (cooltime.get(id) != 0) {
                one = obj.getScore(ChatColor.WHITE + "?????? ?????????");
                one.setScore(a - 5);

                one = obj.getScore(ChatColor.RED + String.valueOf(cooltime.get(id)) + "???");
                one.setScore(a - 6);
            } else {
                one = obj.getScore(ChatColor.RED + "?????? ?????? ??????");
                one.setScore(a - 5);
                if (skill == skills.stealth) {
                    one = obj.getScore(ChatColor.WHITE + "?????? ?????? ??????????????? ?????????");
                    one.setScore(a - 6);
                }
                if (skill == skills.prophet) {
                    one = obj.getScore(ChatColor.WHITE + "?????? ?????????");
                    one.setScore(a - 6);
                }

            }
        }

        player.setScoreboard(board);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, String s, String[] args) {
        if (command.getName().equalsIgnoreCase("des")) {
            sender.sendMessage("???????????? ??????");
            sender.sendMessage("/run??? ?????? ????????? ????????? ????????? ??? ????????????.");
            sender.sendMessage("?????? ????????? ????????? ????????? ????????? ??????");
            if (sender.isOp()) {
                sender.sendMessage("/manhunt??? ?????? ?????? ??????.");
            }
        }
        if (command.getName().equalsIgnoreCase("run")&&!isgaming&&!isready) {
            if (runner.equalsIgnoreCase(sender.getName())) {
                sender.sendMessage("?????? ???????????????.");

            } else {
                runner = sender.getName();

                getServer().sendMessage(Component.text(runner + "?????? ????????? ???????????????."));
            }

        }
        if (command.getName().equalsIgnoreCase("go")) {
            Player pl = (Player) sender;
            go(pl.getUniqueId());

        }
        if (command.getName().equalsIgnoreCase("ab") || command.getName().equalsIgnoreCase("ability")) {
            Player pl = (Player) sender;
            openInventory(pl);

        }
        if (command.getName().equalsIgnoreCase("manhunt")) {

            if (sender.isOp()) {
                isready = true;
                consol.sendMessage(ChatColor.GREEN + "[MANHUNT] ?????? ??????!");
                consol.sendMessage(ChatColor.AQUA + "[MANHUNT] ??????");
                consol.sendMessage(ChatColor.AQUA + "[MANHUNT] ?????" + runner);
                consol.sendMessage(ChatColor.YELLOW + "[MANHUNT] ??????");
                players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                for (int i = 0; i < players.size(); i++) {
                    Player player = (Player) players.get(i);
                    player.setGameMode(GameMode.ADVENTURE);
                    isparty.put(player.getUniqueId(), true);
                    player.getScoreboard().clearSlot(DisplaySlot.SIDEBAR);
                    player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 100));
                    player.addPotionEffect(new PotionEffect(PotionEffectType.SATURATION, 100, 100));
                    if (player.getName().equalsIgnoreCase(runner))//??????
                    {
                        run = player;

                        player.getInventory().clear();
                        player.getInventory().addItem(new ItemStack(Material.COMPASS));
                        player.sendTitle(ChatColor.RED + "????????? ???????????????", ChatColor.GREEN + "????????? ????????? ??????", 40, 120, 40);

                        Random createRandom = new Random();
                        int ability = createRandom.nextInt(5);
                        if (ability == 0) {
                            skill = skills.stealth;
                            player.sendMessage(ChatColor.WHITE + "????????? ?????????" + ChatColor.AQUA + " ?????????" + ChatColor.WHITE + "?????????.");
                        } else if (ability == 1) {
                            skill = skills.covid19;
                            player.sendMessage(ChatColor.WHITE + "????????? ?????????" + ChatColor.GREEN + " ?????????" + ChatColor.WHITE + "?????????.");
                        } else if (ability == 2) {
                            skill = skills.prophet;
                            player.sendMessage(ChatColor.WHITE + "????????? ?????????" + ChatColor.YELLOW + " ?????????" + ChatColor.WHITE + "?????????.");
                        } else if (ability == 3) {
                            skill = skills.bomber;
                            player.sendMessage(ChatColor.WHITE + "????????? ?????????" + ChatColor.YELLOW + " ?????????" + ChatColor.WHITE + "?????????.");
                        } else if (ability == 4) {
                            skill = skills.wolf;
                            player.sendMessage(ChatColor.WHITE + "????????? ?????????" + ChatColor.YELLOW + " ????????????" + ChatColor.WHITE + "?????????.");
                        }
                        openInventory(player);

                        player.sendMessage("???????????? ??????????????? ????????? ?????? ??? ????????????.");
                    } else {
                        player.getInventory().clear();
                        consol.sendMessage(ChatColor.YELLOW + "[MANHUNT] ?????" + player.getName());
                        player.sendTitle(ChatColor.YELLOW + "????????? ???????????????", ChatColor.GREEN + "ready!", 40, 120, 40);
                    }
                }

            }
        }
        return true;
    }
    @EventHandler
    public void EnderDragonDeath(EntityDeathEvent e) {
        if (e.getEntity().getType() == EntityType.ENDER_DRAGON && isgaming) {
            isgaming = false;
            // ????????? ?????? ???????????? ?????? ?????? ?????? ???????????????
        }
    }

    @EventHandler
    public void RunnerDeath(PlayerDeathEvent e) {
        e.setDeathMessage(ChatColor.RED + "????????? ?????????.");
        if (e.getEntity().getPlayer().getName().equalsIgnoreCase(runner) && isgaming && !isrebirthable) {
            isgaming = false;
            e.setDeathSound(Sound.ENTITY_LIGHTNING_BOLT_THUNDER);
            e.getEntity().getPlayer().setGameMode(GameMode.SPECTATOR);

            e.setDeathMessage(ChatColor.RED + "????????? ?????????.");
        } else if (e.getEntity().getPlayer().getName().equalsIgnoreCase(runner) && isgaming && isrebirthable) {
            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++) {
                Player player = (Player) players.get(i);
                player.sendTitle(ChatColor.RED + "????????? ????????? ????????? ??????????????????.", ChatColor.BLUE + "??????");
            }

            e.setCancelled(true);
            e.getEntity().getPlayer().setGameMode(GameMode.SURVIVAL);

            e.getEntity().getPlayer().setHealth(20);
            Random createRandom = new Random();
            int re = createRandom.nextInt(2);

            if (re == 0) {
                isrebirthable = false;
            } else {
                isrebirthable = true;
            }

            e.getEntity().getPlayer().teleport(loc[0]);
            top = 1;


        } else if (!e.getEntity().getName().equalsIgnoreCase(runner)) {
            if (!e.getEntity().getKiller().getName().equalsIgnoreCase(runner)) {
                if (skill == skills.wolf) {
                    getServer().getPlayer(runner).sendTitle("  ", "?????? ??????", 20, 40, 20);
                    Wolf wolf = (Wolf) getServer().getWorld("world").spawnEntity(getServer().getPlayer(runner).getLocation(), EntityType.WOLF);
                    wolf.setOwner(getServer().getPlayer(runner));
                }
            }
        }
    }


    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        e.getPlayer().sendMessage("/des ??? ????????? ??? ??? ????????????.");
        if (!quitcnt.containsKey(e.getPlayer().getUniqueId())) {
            quitcnt.put(e.getPlayer().getUniqueId(), 0);
        }
        if (quitplayer.contains(e.getPlayer().getUniqueId())) {/////////////////////////////
            quitplayer.remove(e.getPlayer().getUniqueId());
        }
        if (deadplayer.contains(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                deadplayer.remove(e.getPlayer().getUniqueId());
                e.getPlayer().setHealth(0);
            }

        }


        if (warnplayer.contains(e.getPlayer().getUniqueId())) {
            if (e.getPlayer().getUniqueId().equals(e.getPlayer().getUniqueId())) {
                warnplayer.remove(e.getPlayer().getUniqueId());
                e.getPlayer().sendTitle("  ", "?????? ??? ???????????? 3???????????????.", 0, 120, 40);
            }

        }

        if (isgaming || isready) {
            try {
                if (isparty.get(e.getPlayer().getUniqueId())) {
                    e.getPlayer().setGameMode(GameMode.SURVIVAL);
                } else {
                    e.getPlayer().setGameMode(GameMode.SPECTATOR);
                }
            } catch (Exception exception) {
                e.getPlayer().setGameMode(GameMode.SPECTATOR);
            }

        } else {
            e.getPlayer().setGameMode(GameMode.SURVIVAL);
        }

    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        if (isparty.get(e.getPlayer().getUniqueId()) && isgaming) {
            e.setQuitMessage(ChatColor.RED + "????????? ???????????? ????????? ???????????????. - " + e.getPlayer().getName() + "\n" + "5??? ?????? ???????????? ????????? ???????????? ???????????????.");

            /////////////////////////////////////
            quitcnt.put(e.getPlayer().getUniqueId(), quitcnt.get(e.getPlayer().getUniqueId()) + 1);
            if (quitcnt.get(e.getPlayer().getUniqueId()) == 4) {
                getServer().sendMessage(Component.text("4??? ???????????? ?????????????????? ?????? ?????????????????????."));
                isparty.put(e.getPlayer().getUniqueId(), false);
                if (e.getPlayer().getName().equalsIgnoreCase(runner)) {
                    isgaming = false;
                    players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                    for (int i = 0; i < players.size(); i++) {
                        Player player = (Player) players.get(i);
                        player.sendTitle("?????? ??????", "????????? ??????", 20, 60, 20);

                    }
                    e.getPlayer().banPlayer("????????? ??????", Date.from(Instant.now().plusSeconds(60 * 60 * 24 * 3)));
                    getServer().sendMessage(Component.text(ChatColor.RED + "????????? ???????????? ????????? ?????????????????????...\n?????? 3??? ???"));
                }

                deadplayer.add(e.getPlayer().getUniqueId());
            }
            if (quitcnt.get(e.getPlayer().getUniqueId()) == 3) {
                warnplayer.add(e.getPlayer().getUniqueId());
            }
            quitplayer.add(e.getPlayer().getUniqueId());
            new BukkitRunnable() {

                @Override
                public void run() {

                    if (!quitplayer.contains(e.getPlayer().getUniqueId())) {
                        return;
                    }
                    getServer().sendMessage(Component.text(ChatColor.RED + "5?????? ???????????????.\n???????????? ?????????????????????"));
                    isparty.put(e.getPlayer().getUniqueId(), false);
                    if (e.getPlayer().getName().equalsIgnoreCase(runner)) {
                        isgaming = false;
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            player.sendTitle("?????? ??????", "", 20, 60, 20);
                        }
                        e.getPlayer().banPlayer("????????? ??????", Date.from(Instant.now().plusSeconds(60 * 60 * 24)));
                        getServer().sendMessage(Component.text(ChatColor.RED + "????????? ???????????? ????????? ?????????????????????...\n?????? 1??? ???"));
                    }
                    deadplayer.add(e.getPlayer().getUniqueId());
                }

            }.runTaskLater(this, 20 * 60 * 5);          //5???
        }
    }


    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
            Player whoHit = (Player) e.getDamager();
            UUID id = whoHit.getUniqueId();
            if (whoHit.getPlayer().getName().equalsIgnoreCase(runner)) {
                go(id);
            } else if (isready) {
                e.setCancelled(true);
            }

        }
    }


    public void go(UUID id) {
        id = getServer().getPlayer(runner).getUniqueId();
        if (isready) {
            isready = false;
            isgaming = true;
            cooltime.put(id, 0);
            players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
            for (int i = 0; i < players.size(); i++) {

                Player player = (Player) players.get(i);
                player.setGameMode(GameMode.SURVIVAL);
                player.setMaxHealth(20);
                player.removePotionEffect(PotionEffectType.INCREASE_DAMAGE);
                if (player.getName().equalsIgnoreCase(runner))//??????
                {

                    if (skill == skills.covid19) {
                        isrebirthable = false;
                        player.sendMessage("????????? ????????? ??????????????????");
                        player.setMaxHealth(14);
                        player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100000000, 0));
                    } else if (skill == skills.stealth) {
                        player.setMaxHealth(20);
                        player.sendMessage("????????? ????????? ??????????????????");
                        isrebirthable = false;
                    } else if (skill == skills.prophet) {
                        player.setMaxHealth(20);
                        player.sendMessage("????????? ????????? ??????????????????");
                        isrebirthable = true;
                    } else if (skill == skills.bomber) {
                        player.setMaxHealth(20);
                        player.sendMessage("????????? ????????? ??????????????????");
                        isrebirthable = false;
                    } else if (skill == skills.wolf) {
                        player.setMaxHealth(20);
                        player.sendMessage("????????? ????????? ?????????????????????");

                        isrebirthable = false;
                    }
                    player.getInventory().clear();
                    player.sendTitle(ChatColor.RED + "????????????!", ChatColor.GREEN + "????????? ?????? ?????? ???????????? ????????????", 40, 120, 40);
                } else {
                    player.getInventory().clear();
                    player.getInventory().addItem(new ItemStack(Material.COMPASS, 1));
                    consol.sendMessage(ChatColor.YELLOW + "[MANHUNT] ?????" + player.getName());
                    player.sendTitle(ChatColor.YELLOW + "?????? ??????", ChatColor.GREEN + "????????? ????????????", 40, 120, 40);
                }
            }
        }
    }


    @EventHandler
    public void damadge(EntityDamageEvent e) {
        if (e.getEntityType() == EntityType.PLAYER) {
            if (e.getEntity().getName().equalsIgnoreCase(runner) && skill == skills.bomber) {
                if (e.getCause() == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.getCause() == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
                    e.setDamage(0);
                }
                if (e.getCause() == EntityDamageEvent.DamageCause.FALL) {
                    e.setDamage(e.getDamage() * 0.25);
                }
            }
        }
    }

    @EventHandler
    public void PlayerClickBlock(PlayerInteractEvent e) {

        Player p = e.getPlayer(); // ??????????????? ????????? ???????????? ???????????? ?????? (Ex: ?????????, ????????? ?????? ??????)
        if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) && p.getInventory().getItemInMainHand().getType() == Material.COMPASS) {
            if (e.getPlayer().getName().equalsIgnoreCase(runner) && isready) {
                openInventory(e.getPlayer());
            }
        }

        if (!isgaming) {
            e.setCancelled(true);
        }
        if (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK) || e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            if (e.getPlayer().getInventory().getItemInMainHand() != null || e.getPlayer().getName().equalsIgnoreCase(runner)) {
                ItemStack firstitem = p.getInventory().getItemInMainHand().asOne();
                if (p.getInventory().getItemInMainHand().getType() == Material.FLINT_AND_STEEL && (e.getAction().equals(Action.LEFT_CLICK_AIR) || e.getAction().equals(Action.LEFT_CLICK_BLOCK))) {
                    if (skills.bomber == skill) {
                        getServer().getPlayer(runner).getWorld().spawnParticle(Particle.LAVA, getServer().getPlayer(runner).getLocation(), 20, 1.2F, 1F, 1.2F);
                        p.getWorld().spawnEntity(p.getLocation(), EntityType.PRIMED_TNT);
                        p.getInventory().removeItem(firstitem);
                    }
                }

                if (p.getInventory().getItemInMainHand().getType() == Material.IRON_INGOT) {
                    if (skills.stealth == skill && cooltime.get(p.getUniqueId()) != 0) {

                        p.sendActionBar(ChatColor.RED + "????????? ????????? ??? ????????????.");

                    }
                    if (skills.stealth == skill && cooltime.get(p.getUniqueId()) == 0)//?????? ??????&&??? 0
                    {
                        cooltime.put(p.getUniqueId(), 70);
                        p.getInventory().removeItem(firstitem);
                        getServer().getPlayer(runner).getWorld().spawnParticle(Particle.FLASH, getServer().getPlayer(runner).getLocation(), 10, 1.2F, 1F, 1.2F);
                        p.sendTitle("  ", "?????? ??????!", 20, 40, 20);
                        compass = 15;
                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            if (!player.getName().equalsIgnoreCase(runner))//??????
                            {
                                player.removePotionEffect(PotionEffectType.SLOW);
                                player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0));
                            }
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.CLOCK) {
                    if (skills.prophet == skill && cooltime.get(p.getUniqueId()) != 0) {

                        p.sendActionBar(ChatColor.RED + "????????? ????????? ??? ????????????.");

                    }
                    if (skills.prophet == skill && cooltime.get(p.getUniqueId()) == 0) {
                        cooltime.put(p.getUniqueId(), 500);
                        p.getInventory().removeItem(firstitem);
                        p.sendTitle("  ", "?????? ??????!", 20, 40, 20);
                        compass = 15;
                        if (top > 5) {
                            p.teleport(loc[top - 5]);
                            top = -5;
                        } else {
                            p.teleport(loc[0]);
                            top = 1;
                        }
                    }
                }
                if (p.getInventory().getItemInMainHand().getType() == Material.DIAMOND) {
                    if (skills.stealth == skill && cooltime.get(p.getUniqueId()) != 0) {
                        p.sendActionBar(ChatColor.RED + "????????? ????????? ??? ????????????.");
                    }
                    if (skills.stealth == skill && cooltime.get(p.getUniqueId()) == 0)//?????? ??????&&??? 0
                    {
                        getServer().getPlayer(runner).getWorld().spawnParticle(Particle.WHITE_ASH, getServer().getPlayer(runner).getLocation(), 500, 1.2F, 1F, 1.2F);
                        getServer().getPlayer(runner).getWorld().spawnParticle(Particle.SPELL_MOB_AMBIENT, getServer().getPlayer(runner).getLocation(), 500, 1.2F, 1F, 1.2F);
                        p.sendTitle("  ", "?????? ??????!", 20, 40, 20);
                        cooltime.put(p.getUniqueId(), 120);
                        compass = 15;
                        p.removePotionEffect(PotionEffectType.SPEED);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 0));
                        p.removePotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
                        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 200, 0));
                        p.getInventory().removeItem(firstitem);


                        players = Arrays.asList(Bukkit.getOnlinePlayers().toArray());
                        for (int i = 0; i < players.size(); i++) {
                            Player player = (Player) players.get(i);
                            if (!player.getName().equalsIgnoreCase(runner))//??????
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
        }


    }

    @EventHandler
    public void respawn(PlayerRespawnEvent e) {
        e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
    }


    public Hypixel() {
        inv = Bukkit.createInventory(null, 9, "ability");
    }

    public void initializeItems() {
        inv.clear();
        if (skill == skills.stealth) {
            inv.addItem(createGuiItem(Material.IRON_INGOT,
                    ChatColor.RED + "?????????", ChatColor.GREEN + "??????", ChatColor.WHITE + "???????????? ????????? ???????????? ??????????????????", ChatColor.WHITE + "??????1??? ????????????.", "  ", ChatColor.GREEN + "???????????????", ChatColor.WHITE + "???????????? ???????????? ????????? ????????? ?????? ????????????", ChatColor.WHITE + "??????????????? ????????? ????????? ????????????.", "  ", ChatColor.GREEN + "[Selected]"));
        } else {
            inv.addItem(createGuiItem(Material.IRON_INGOT, ChatColor.RED + "?????????", ChatColor.GREEN + "??????", ChatColor.WHITE + "???????????? ????????? ???????????? ??????????????????", ChatColor.WHITE + "??????1??? ????????????.", "  ", ChatColor.GREEN + "???????????????", ChatColor.WHITE + "???????????? ???????????? ????????? ????????? ?????? ????????????", ChatColor.WHITE + "??????????????? ????????? ????????? ????????????.", "  ", ChatColor.YELLOW + "[Select to click]"));
        }
        if (skill == skills.covid19) {
            inv.addItem(createGuiItem(Material.GOLD_INGOT, ChatColor.RED + "?????????", ChatColor.GREEN + "????????????", ChatColor.WHITE + "?????? 5??? ????????? ??????????????????", ChatColor.WHITE + "????????? ?????? ???????????????", ChatColor.GREEN + "", "??? 2??? ????????????,????????? 7????????????", "  ", ChatColor.GREEN + "[Selected]"));
        } else {
            inv.addItem(createGuiItem(Material.GOLD_INGOT, ChatColor.RED + "?????????", ChatColor.GREEN + "????????????", ChatColor.WHITE + "?????? 5??? ????????? ??????????????????", ChatColor.WHITE + "????????? ?????? ???????????????", ChatColor.GREEN + "?????????", "??? 2??? ????????????,????????? 7????????????", "  ", ChatColor.YELLOW + "[Select to click]"));
        }

        if (skill == skills.prophet) {
            inv.addItem(createGuiItem(Material.CLOCK, ChatColor.RED + "?????????", ChatColor.GREEN + "??????...?", ChatColor.WHITE + "????????? ??????????????? ???????????????.", ChatColor.GREEN + "???????????????", ChatColor.WHITE + "?????? 1?????? ????????? 5??? ?????? ????????? ?????????.", "  ", ChatColor.GREEN + "[Selected]"));
        } else {
            inv.addItem(createGuiItem(Material.CLOCK, ChatColor.RED + "?????????", ChatColor.GREEN + "??????...?", ChatColor.WHITE + "????????? ??????????????? ???????????????.", ChatColor.GREEN + "???????????????", ChatColor.WHITE + "?????? 1?????? ????????? 5??? ?????? ????????? ?????????.", "  ", ChatColor.YELLOW + "[Select to click]"));
        }


        if (skill == skills.bomber) {
            inv.addItem(createGuiItem(Material.TNT, ChatColor.RED + "?????????", ChatColor.GREEN + "?????????",
                    ChatColor.WHITE + "???????????? ?????????",
                    ChatColor.WHITE + "????????? ????????? tnt??? ???????????????(????????? ??????)",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "?????????????????? ????????????.",
                    ChatColor.WHITE + "?????? ???????????? 25%??? ???????????????.",
                    "  ",
                    ChatColor.GREEN + "[Selected]"));
        } else {
            inv.addItem(createGuiItem(Material.TNT, ChatColor.RED + "?????????", ChatColor.GREEN + "?????????",
                    ChatColor.WHITE + "???????????? ?????????",
                    ChatColor.WHITE + "????????? ????????? tnt??? ???????????????(????????? ??????)",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "?????????????????? ????????????.",
                    ChatColor.WHITE + "?????? ???????????? 25%??? ???????????????.",
                    "  ",
                    ChatColor.YELLOW + "[Select to click]"));
        }

        if (skill == skills.wolf) {
            inv.addItem(createGuiItem(Material.WOLF_SPAWN_EGG, ChatColor.RED + "????????????",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "30?????????",
                    ChatColor.WHITE + "???????????? 3????????? ????????????.",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "???????????? ????????? 1.25??? ?????????",
                    ChatColor.WHITE + "?????? 2????????? ??????",
                    ChatColor.WHITE + "??? 2????????? ????????????",
                    "  ",
                    ChatColor.GREEN + "[Selected]"));
        } else {
            inv.addItem(createGuiItem(Material.WOLF_SPAWN_EGG, ChatColor.RED + "????????????",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "30?????????",
                    ChatColor.WHITE + "???????????? 3????????? ????????????.",
                    ChatColor.DARK_PURPLE + "?????????",
                    ChatColor.WHITE + "???????????? ????????? 1.25??? ?????????",
                    ChatColor.WHITE + "?????? 2????????? ??????",
                    ChatColor.WHITE + "??? 2????????? ????????????",
                    "  ",
                    ChatColor.YELLOW + "[Select to click]"));
        }
        inv.addItem(createGuiItem(Material.YELLOW_STAINED_GLASS_PANE, "  ", "  "));
        inv.addItem(createGuiItem(Material.GREEN_STAINED_GLASS_PANE, "  ", "  "));
        inv.addItem(createGuiItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE, "  ", "  "));
        inv.addItem(createGuiItem(Material.BARRIER, "??????", ""));
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
        if (isready && ent.getName().equalsIgnoreCase(runner)) {
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


        if (e.getRawSlot() == 0) {
            p.sendMessage("????????? ????????? ????????? ?????????????????????.");
            skill = skills.stealth;
        } else if (e.getRawSlot() == 1) {
            p.sendMessage("????????? ????????? ????????? ????????? ?????????????????????.");
            skill = skills.covid19;
        } else if (e.getRawSlot() == 2) {
            p.sendMessage("????????? ????????? ????????? ?????????????????????.");
            skill = skills.prophet;
        } else if (e.getRawSlot() == 3) {
            p.sendMessage("????????? ????????? ????????? ?????????????????????.");
            skill = skills.bomber;
        } else if (e.getRawSlot() == 4) {
            p.sendMessage("????????? ???????????? ????????? ?????????????????????.");
            skill = skills.wolf;
        }


        if (e.getRawSlot() == 8) {
            p.closeInventory();
        }
        initializeItems();
    }

    @EventHandler
    public void onInventoryClick(final InventoryDragEvent e) {
        if (e.getInventory().equals(inv)) {
            e.setCancelled(true);
        }
    }
}
