package com.cily.utils.media;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;

public class Player{
	private MediaPlayer player;
	
	public void start(Context cx, int resId, boolean loop){
		if(player != null){
			player.reset();
			player.release();
		}
		
		player = MediaPlayer.create(cx, resId);
		
		if(player == null){
			return;
		}
		
		if(player.isPlaying()){
			return;
		}
		
		player.setLooping(loop);
		player.start();
		if(l != null){
			l.onCompletion(player);
		}
	}
	
	public void pause(){
		if(player != null){
			player.pause();
		}
	}
	
	public void stop(){
		if(player != null){
			player.stop();
		}
	}
	
	public void release(){
		if(player != null){
			player.release();
		}
	}
	
	private OnCompletionListener l;
	public void addCompletListener(OnCompletionListener l){
		this.l = l;
	}
}