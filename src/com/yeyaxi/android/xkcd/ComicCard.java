package com.yeyaxi.android.xkcd;

import com.fima.cardsui.objects.Card;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class ComicCard extends Card{
	private ImageView iv;
	private Bitmap image;
//	private long cardId;
	public ComicCard(String title, Bitmap image, long cardId){
		super(title, String.valueOf(cardId));
		this.image = image;
//		this.cardId = cardId;
	}

	@Override
	public View getCardContent(Context context) {
		View view = LayoutInflater.from(context).inflate(R.layout.card_picture, null);

		((TextView) view.findViewById(R.id.title)).setText(title);
//		((ImageView) view.findViewById(R.id.imageView1)).setImageBitmap(image);
		iv = (ImageView) view.findViewById(R.id.imageView1);
		setCardImageBitmap();
		return view;
	}
	
	private void setCardImageBitmap() {
		iv.setImageBitmap(image);
	}
	
	/**
	 * Return the comic ID
	 */
	public long getCardId() {
		return Long.parseLong(this.desc);
	}
}
