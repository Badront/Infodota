package com.badr.infodota;

import com.badr.infodota.dao.AbilityDao;
import com.badr.infodota.dao.AccountDao;
import com.badr.infodota.dao.CreateTableDao;
import com.badr.infodota.dao.HeroDao;
import com.badr.infodota.dao.HeroStatsDao;
import com.badr.infodota.dao.ItemDao;
import com.badr.infodota.dao.StreamDao;
import com.badr.infodota.dao.TeamDao;
import com.badr.infodota.remote.cosmetic.CosmeticsRemoteEntityService;
import com.badr.infodota.remote.cosmetic.CosmeticsRemoteEntityServiceImpl;
import com.badr.infodota.remote.counterpicker.CounterRemoteEntityServiceImpl;
import com.badr.infodota.remote.joindota.JoinDotaRemoteServiceImpl;
import com.badr.infodota.remote.match.MatchRemoteEntityServiceImpl;
import com.badr.infodota.remote.news.NewsRemoteServiceImpl;
import com.badr.infodota.remote.player.PlayerRemoteServiceImpl;
import com.badr.infodota.remote.team.TeamRemoteServiceImpl;
import com.badr.infodota.remote.ti4.TI4RemoteServiceImpl;
import com.badr.infodota.remote.twitch.TwitchRemoteServiceImpl;
import com.badr.infodota.service.UpdateService;
import com.badr.infodota.service.cosmetic.CounterServiceImpl;
import com.badr.infodota.service.counterpicker.CosmeticService;
import com.badr.infodota.service.counterpicker.CosmeticServiceImpl;
import com.badr.infodota.service.hero.HeroServiceImpl;
import com.badr.infodota.service.item.ItemServiceImpl;
import com.badr.infodota.service.joindota.JoinDotaServiceImpl;
import com.badr.infodota.service.match.MatchServiceImpl;
import com.badr.infodota.service.news.NewsService;
import com.badr.infodota.service.news.NewsServiceImpl;
import com.badr.infodota.service.player.PlayerServiceImpl;
import com.badr.infodota.service.team.TeamServiceImpl;
import com.badr.infodota.service.ti4.TI4ServiceImpl;
import com.badr.infodota.service.twitch.TwitchServiceImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * User: ABadretdinov
 * Date: 02.04.14
 * Time: 10:51
 */
public class BeanContainer implements InitializingBean {
    private static final Object MONITOR = new Object();
    private static BeanContainer instance = null;

    private CosmeticsRemoteEntityServiceImpl cosmeticsRemoteEntityService;
    private CosmeticServiceImpl cosmeticService;

    private CounterRemoteEntityServiceImpl counterRemoteEntityService;
    private CounterServiceImpl counterService;

    private PlayerRemoteServiceImpl playerRemoteService;
    private PlayerServiceImpl playerService;
    private AccountDao accountDao;

    private MatchRemoteEntityServiceImpl matchRemoteEntityService;
    private MatchServiceImpl matchService;

    private NewsRemoteServiceImpl newsRemoteService;
    private NewsServiceImpl newsService;

    private JoinDotaRemoteServiceImpl joinDotaRemoteService;
    private JoinDotaServiceImpl joinDotaService;

    private TI4RemoteServiceImpl ti4RemoteService;
    private TI4ServiceImpl ti4Service;

    private TeamRemoteServiceImpl teamRemoteService;
    private TeamServiceImpl teamService;
    private TeamDao teamDao;

    private TwitchRemoteServiceImpl twitchRemoteService;
    private TwitchServiceImpl twitchService;
    private StreamDao streamDao;

    private HeroServiceImpl heroService;
    private HeroDao heroDao;
    private HeroStatsDao heroStatsDao;
    private AbilityDao abilityDao;

    private ItemServiceImpl itemService;
    private ItemDao itemDao;

    private UpdateService updateService;

    private List<CreateTableDao> allDaos;

    public BeanContainer() {

        allDaos = new ArrayList<>();

        heroDao = new HeroDao();
        heroStatsDao = new HeroStatsDao();
        abilityDao = new AbilityDao();
        itemDao = new ItemDao();
        accountDao = new AccountDao();
        streamDao = new StreamDao();
        teamDao = new TeamDao();

        allDaos.add(heroDao);
        allDaos.add(heroStatsDao);
        allDaos.add(itemDao);
        allDaos.add(accountDao);
        allDaos.add(abilityDao);
        allDaos.add(streamDao);
        //todo updated_version
        allDaos.add(teamDao);

        updateService = new UpdateService();

        cosmeticsRemoteEntityService = new CosmeticsRemoteEntityServiceImpl();
        cosmeticService = new CosmeticServiceImpl();

        counterRemoteEntityService = new CounterRemoteEntityServiceImpl();
        counterService = new CounterServiceImpl();

        playerRemoteService = new PlayerRemoteServiceImpl();
        playerService = new PlayerServiceImpl();

        matchRemoteEntityService = new MatchRemoteEntityServiceImpl();
        matchService = new MatchServiceImpl();

        newsRemoteService = new NewsRemoteServiceImpl();
        newsService = new NewsServiceImpl();

        joinDotaRemoteService = new JoinDotaRemoteServiceImpl();
        joinDotaService = new JoinDotaServiceImpl();

        ti4RemoteService = new TI4RemoteServiceImpl();
        ti4Service = new TI4ServiceImpl();

        teamRemoteService = new TeamRemoteServiceImpl();
        teamService = new TeamServiceImpl();

        twitchRemoteService = new TwitchRemoteServiceImpl();
        twitchService = new TwitchServiceImpl();

        heroService = new HeroServiceImpl();

        itemService = new ItemServiceImpl();
    }

    public static BeanContainer getInstance() {
        if (instance != null) {
            return instance;
        }
        synchronized (MONITOR) {
            if (instance == null) {
                instance = new BeanContainer();
                instance.initialize();
            }
        }
        return instance;
    }

    @Override
    public void initialize() {
        heroService.initialize();
        itemService.initialize();
        cosmeticService.initialize();
        counterService.initialize();
        playerService.initialize();
        matchService.initialize();
        newsService.initialize();
        joinDotaService.initialize();
        ti4Service.initialize();
        teamService.initialize();
        twitchService.initialize();
    }

    public CosmeticsRemoteEntityService getCosmeticsRemoteEntityService() {
        return cosmeticsRemoteEntityService;
    }

    public CosmeticService getCosmeticService() {
        return cosmeticService;
    }

    public UpdateService getUpdateService() {
        return updateService;
    }

    public CounterRemoteEntityServiceImpl getCounterRemoteEntityService() {
        return counterRemoteEntityService;
    }

    public CounterServiceImpl getCounterService() {
        return counterService;
    }

    public PlayerServiceImpl getPlayerService() {
        return playerService;
    }

    public PlayerRemoteServiceImpl getPlayerRemoteService() {
        return playerRemoteService;
    }

    public MatchRemoteEntityServiceImpl getMatchRemoteEntityService() {
        return matchRemoteEntityService;
    }

    public MatchServiceImpl getMatchService() {
        return matchService;
    }

    public NewsRemoteServiceImpl getNewsRemoteService() {
        return newsRemoteService;
    }

    public NewsService getNewsService() {
        return newsService;
    }

    public JoinDotaRemoteServiceImpl getJoinDotaRemoteService() {
        return joinDotaRemoteService;
    }

    public JoinDotaServiceImpl getJoinDotaService() {
        return joinDotaService;
    }

    public TI4RemoteServiceImpl getTi4RemoteService() {
        return ti4RemoteService;
    }

    public TI4ServiceImpl getTi4Service() {
        return ti4Service;
    }

    public TeamServiceImpl getTeamService() {
        return teamService;
    }

    public TeamRemoteServiceImpl getTeamRemoteService() {
        return teamRemoteService;
    }

    public TwitchRemoteServiceImpl getTwitchRemoteService() {
        return twitchRemoteService;
    }

    public TwitchServiceImpl getTwitchService() {
        return twitchService;
    }

    public HeroDao getHeroDao() {
        return heroDao;
    }

    public HeroStatsDao getHeroStatsDao() {
        return heroStatsDao;
    }

    public ItemDao getItemDao() {
        return itemDao;
    }

    public AbilityDao getAbilityDao() {
        return abilityDao;
    }

    public List<CreateTableDao> getAllDaos() {
        return allDaos;
    }

    public HeroServiceImpl getHeroService() {
        return heroService;
    }

    public ItemServiceImpl getItemService() {
        return itemService;
    }

    public StreamDao getStreamDao() {
        return streamDao;
    }

    public AccountDao getAccountDao() {
        return accountDao;
    }

    public TeamDao getTeamDao() {
        return teamDao;
    }
}
