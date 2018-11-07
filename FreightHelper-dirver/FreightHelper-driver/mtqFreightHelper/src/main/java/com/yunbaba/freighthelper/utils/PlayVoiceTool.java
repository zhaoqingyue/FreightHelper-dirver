package com.yunbaba.freighthelper.utils;

import java.io.IOException;

import android.content.Context;
import android.media.MediaPlayer;

public class PlayVoiceTool {

	// public File mAudioFile = null;
	private MediaPlayer mMediaPlayer;

	public boolean isRecording = false;

	private static PlayVoiceTool instance = new PlayVoiceTool();

	public static PlayVoiceTool getInstance() {
		return instance;
	}

	public PlayVoiceTool() {

		// TODO Auto-generated constructor stub

		if (mMediaPlayer == null)
			mMediaPlayer = new MediaPlayer();

	}

	// public void Play(){
	// mMediaPlayer.reset();
	// try {
	// mMediaPlayer.setDataSource(mAudioFile.getAbsolutePath());
	// if (!mMediaPlayer.isPlaying()) {
	// mMediaPlayer.prepare();
	// mMediaPlayer.start();
	// }
	// } catch (IllegalArgumentException e) {
	//
	// e.printStackTrace();
	// } catch (SecurityException e) {
	//
	// e.printStackTrace();
	// } catch (IllegalStateException e) {
	//
	// e.printStackTrace();
	// } catch (IOException e) {
	//
	// e.printStackTrace();
	// }
	// }
	//

	public void PlayWithFilePath(String FilePath, final OnPlayStatusListener listener) {

		if (mMediaPlayer == null)
			mMediaPlayer = new MediaPlayer();

		mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (listener != null) {
                    listener.onStop();
                }
            }
        });

		mMediaPlayer.reset();
		try {
			mMediaPlayer.setDataSource(FilePath);


			if (!mMediaPlayer.isPlaying()) {
				mMediaPlayer.prepare();
				mMediaPlayer.start();

                if (listener != null) {
                    listener.onStart();
                }


			}


		} catch (IllegalArgumentException e) {

			e.printStackTrace();
		} catch (SecurityException e) {

			e.printStackTrace();
		} catch (IllegalStateException e) {

			e.printStackTrace();
		} catch (IOException e) {

			e.printStackTrace();
		}
	}

	public void Stop() {

		if (mMediaPlayer != null) {
			mMediaPlayer.reset();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}

	}

	public CharSequence getFileTimeLength(Context context, String speechids) {


		String path = DownloadUtil.getSpeechFilePathById(context, speechids);

		if (path != null) {

			try {
				mMediaPlayer.setDataSource(path);
				mMediaPlayer.prepare();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (SecurityException e) {

				e.printStackTrace();
			} catch (IllegalStateException e) {

				e.printStackTrace();
			} catch (IOException e) {

				e.printStackTrace();
			}

			return ((float) mMediaPlayer.getDuration()) / 1000f + "\"";
		} else

			return "";
	}

	public interface OnPlayStatusListener {

	    void onStart();

	    void onStop();

    }
}
