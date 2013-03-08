package uk.ac.bbk.dcs.ecoapp.utility;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Dictionary;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

/**
 * This class is responsible for asynchronously fetching images from the web.
 * By default images are cached in memory though this can be disabled.
 * The caller may also specify a default image to be used until the image has loaded
 * 
 * @author Dave Durbin
 *
 */
public class AsynchImageLoader {
	/** Singleton instance */
	private static AsynchImageLoader imageLoader_;

	/** Cache for images. Images are stored by URL string as key */
	private Dictionary<String, Bitmap> imageCache_;

	/**
	 * Private constructor to prevent multiple copies being constructed
	 */
	private AsynchImageLoader( ) {}


	/**
	 * Synchronized to ensure that we have a singleton.
	 * Remember...double checked locking is broken!
	 * @return The singleton instance of the image loader
	 */
	public synchronized static AsynchImageLoader getSingletonInstance( ) {
		if(  imageLoader_ == null ) {
			imageLoader_ = new AsynchImageLoader( );
		}
		return imageLoader_;
	}

	/**
	 * Load an image asynchronously and put it into the provided ImageView
	 * @param urlString The URL of the image as a String
	 * @param imageView The ImageView into which to put the image
	 * The image is cached by default. If a cached version exists it is used.
	 */
	public void loadToImageView( final String imageURLString, final ImageView imageView ) {
		// First check for an existing cached copy and use it if it's there
		Bitmap bitmap = null;
		synchronized( imageCache_) {
			bitmap = imageCache_.get(imageURLString);
		}

		// If there's no bitmap, launch a Thread to get it
		if( bitmap == null ) {
			Thread imageLoaderThread = new Thread( 
					new Runnable( ){
						public void run( ) {
							Bitmap bitmap = null;
							InputStream bitmapInputStream = null;
							URL imageURL = null;

							try {
								imageURL = new URL( imageURLString );
								// Open connection to image
								URLConnection connection = imageURL.openConnection();

								// TODO For safety we should check that the image is actually an image

								// Retrieve Bitmap
								bitmapInputStream = connection.getInputStream();
								bitmap = BitmapFactory.decodeStream(bitmapInputStream);

								// Stash the recovered bitmap in the cache
								synchronized( imageCache_ ) {
									imageCache_.put(imageURLString, bitmap);
								}
								final Bitmap callbackBitmap = bitmap;
								imageView.post(
										new Runnable( ) {
											public void run( ) {
												imageView.setImageBitmap(callbackBitmap);
											}
										});
							} catch( MalformedURLException e) {
								Log.e(getClass().getCanonicalName(), "Invalid URL for image loader "+imageURLString, e);
							} catch (IOException e) {
								Log.e(getClass().getCanonicalName(), "Couldn't load logo from URL "+imageURL.toString(), e);
							} finally {
								if( bitmapInputStream != null ) {
									try {
										bitmapInputStream.close( );
									} catch (IOException e) {}
								}
							}
						} // end of run()
					},  // end of new Runnable( )
					"Image Loader"); // end of new Thread )
			imageLoaderThread.start( );
		} else {
			// We have a bitmap, post a call to the UI thread to update it
			final Bitmap callbackBitmap = bitmap;
			imageView.post(
					new Runnable( ) {
						public void run( ) {
							imageView.setImageBitmap(callbackBitmap);
						}
					});
		}
	}
}
