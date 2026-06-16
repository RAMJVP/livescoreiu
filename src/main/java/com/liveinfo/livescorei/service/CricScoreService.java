package com.liveinfo.livescorei.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.liveinfo.livescorei.LiveMatchClient;
import com.liveinfo.livescorei.LiveServiceClient;
import com.liveinfo.livescorei.model.CricScore;
import com.liveinfo.livescorei.model.MatchBoard;
import com.liveinfo.livescorei.model.MatchBoardInfo;
import com.liveinfo.livescorei.util.DateTimeUtil;


@Service
public class CricScoreService {

	@Autowired
	private LiveServiceClient liveServiceClient;
	
	
	@Autowired
	private LiveMatchClient liveMatchClient;
	
   @Autowired 
   private Map<LocalDate, List<MatchBoard>> cacheMap;
   
   @Autowired 
   private DateTimeService dateTimeService;
	 

  // @Cacheable(value = "methodCache", key = "#root.method.name")
	public CricScore geCricScoreList() {
	   System.out.println("geCricScoreList " + java.time.LocalTime.now());
		return getFromServer();
	}


	private CricScore getFromServer() {
		System.out.println("Computed value at " + java.time.LocalTime.now());
		CricScore filterdScore=new CricScore.Builder().build();
	//	if(false) {
		try {
			CricScore cricScoreList=liveServiceClient.getCricScore();
			LocalDate currentDate = LocalDate.now().plusDays(0);// on particular day start and end
			
			/*
			 * filterdScore= new
			 * CricScore.Builder().withApikey(cricScoreList.getApikey()).withData(
			 * cricScoreList.getData().stream().filter
			 * (m->m.getSeries().equals("ICC Mens T20 World Cup 2024") &&
			 * m.getMatchType().equals("t20") &&
			 * DateTimeUtil.compareLocalDateTimeAndZonedDateTime(m.getDateTimeGMT(),
			 * dateTimeService.toGmtZonedDateTime(currentDate))>= 0 &&
			 * DateTimeUtil.compareLocalDateTimeAndZonedDateTime(m.getDateTimeGMT(),
			 * dateTimeService.toGmtZonedDateTimeEnd(currentDate)) <= 0).
			 * collect(Collectors.toList())).withStatus(cricScoreList.getStatus()).withInfo(
			 * cricScoreList.getInfo()).build();
			 */
			
			 
			 filterdScore= new CricScore.Builder().withApikey(cricScoreList.getApikey()).withData(cricScoreList.getData().stream().filter
						(m->m.getSeries().equals("ICC Mens T20 World Cup 2024") && m.getMatchType().equals("t20")
								&&
								  DateTimeUtil.compareLocalDateTimeAndZonedDateTime(m.getDateTimeGMT(),
								  dateTimeService.toGmtZonedDateTime(currentDate))>= 0
						 ).
						collect(Collectors.toList())).withStatus(cricScoreList.getStatus()).withInfo(cricScoreList.getInfo()).withLive1(false).build();
				
			 
			 //System.out.println("cricScoreList "+cricScoreList);
			
		}catch(Exception e) {
			System.err.println("error received "+ e.getCause());
		}

	//	}
		
		return filterdScore;
	}


	public MatchBoard getMatch() {
		List<MatchBoard> ms=cacheMap.get(LocalDate.now());
		if(Optional.ofNullable(cacheMap.get(LocalDate.now())).isPresent()) {
			return ms.get(0);
		}
		//System.out.println("cacheMap nomatch "+cacheMap);
		return new MatchBoard.Builder().build();
		
	}
	
	/*
	 * @Autowired private ThresholdService thresholdService;
	 */

	@Autowired
	private APIKeyService apiKeyService;
	@Autowired
	private BreakService breakService;
	
	int run=80;
	@Cacheable(value = "methodCache", key = "#root.method.name")
	public MatchBoardInfo getMatchBoardInfo(String matchId) {
		System.out.println("Computed value at getMatchBoardInfo@ " + java.time.LocalTime.now());
		//comment test only
		/*
		 * List<Score> scores=List.of(new
		 * Score.Builder().withInning(matchId).witho(20).withr(100).withw(2).build(),
		 * new
		 * Score.Builder().withInning(matchId).witho(18).withr(++run).withw(4).build());
		 * 
		 * Data data=new
		 * Data.Builder().withName("IND vs AUS, 17th Match, Group B").withTeams(List.of(
		 * "IND","AUS")).withScore(scores).build();
		 */
		MatchBoardInfo error=new MatchBoardInfo.Builder().withStatus("error").build();
		
		// "status": "Innings Break",
		//if(false) {
		try {
			if(!Optional.ofNullable(apiKeyService.getKey()).isPresent()) {
				System.err.println("error received - no api key");
				return error;
			}
			if(breakService.inningsBreak()) {
				System.out.println("Innings Break. resumed after 10 mins");
				MatchBoardInfo inningsBreak=new MatchBoardInfo.Builder().withStatus("Innings Break").build();
				return inningsBreak;
			}
			//apikey=1276331d-5e40-48e0-9a02-e7035489c3e6&id=c99e9832-62a5-495d-b33f-f3a149f9441e
			String apiKey=apiKeyService.getKey();
			MatchBoardInfo matchBoardInfo=liveMatchClient.matchInfo(apiKey, matchId);
			int hitsT=matchBoardInfo.getInfo().getHitsToday(); 
			System.out.println("hitsT "+hitsT);
			  if(thresholdCheck(hitsT)) { 
				  System.out.println("hit >= 90");
				  apiKeyService.updateKey(apiKeyService.apiKey(apiKey));
				  }
			  
			  String status=matchBoardInfo.getStatus(); 
			  if(breakCheck(status)) {
				  breakService.updateKey(LocalDateTime.now());
			  }
			  
			return matchBoardInfo;
		}catch(Exception e) {
			System.err.println("error received "+ e.getCause());
			
		}
		//}
		return error;
	}


	private boolean breakCheck(String status) {
		return Optional.ofNullable(status).isPresent() && status.equals("Innings Break");
	}


	private boolean thresholdCheck(int hitsT) {
		return hitsT>=90;
	}
	
}
