package com.liveinfo.livescorei.model;

import java.util.List;
import java.util.Collections;

public class CricScore {
    private String apikey;//apikey 
    private List<MatchData> data;
    private String status;
    private Info info;
     private boolean live;
    
     CricScore(){
    	
    }

	private CricScore(Builder builder) {
		this.apikey = builder.apikey;
		this.data = builder.data;
		this.status = builder.status;
		this.info = builder.info;
		this.live = builder.live;
		 
	}

    // Getters and Setters
    public String getApikey() {
        return apikey;
    }
public boolean getLive() {
        return live;
    }
    public void setApikey(String apikey) {
        this.apikey = apikey;
    }
    
     public void setlive(boolean live) {
        this.live = live;
    }

    public List<MatchData> getData() {
        return data;
    }

    public void setData(List<MatchData> data) {
        this.data = data;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Info getInfo() {
        return info;
    }

    public void setInfo(Info info) {
        this.info = info;
    }

	public static Builder builder() {
		return new Builder();
	}

	public static final class Builder {
		private String apikey;
		private List<MatchData> data = Collections.emptyList();
		private String status;
		private Info info;
    private boolean live;
 
		public Builder() {
		}

		public Builder withApikey(String apikey) {
			this.apikey = apikey;
			return this;
		}

		public Builder withData(List<MatchData> data) {
			this.data = data;
			return this;
		}

		public Builder withStatus(String status) {
			this.status = status;
			return this;
		}

		public Builder withInfo(Info info) {
			this.info = info;
			return this;
		}
public Builder withLive(boolean live) {
			this.live = live;
			return this;
		}
		public CricScore build() {
			return new CricScore(this);
		}
	}
    
    
    
    
    
    
    
    
}

