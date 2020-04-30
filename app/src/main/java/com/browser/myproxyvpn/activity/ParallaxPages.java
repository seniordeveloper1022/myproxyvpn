/*
 * Copyright 2016 Matthew Tamlin
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.browser.myproxyvpn.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.browser.myproxyvpn.R;

public class ParallaxPages extends Fragment {
	/**
	 * Used to identify this class during debugging.
	 */
	@SuppressWarnings("unused")
	private static final String TAG = "[ParallaxPage]";

	/**
	 * The root view of this Fragment.
	 */
	private FrameLayout rootView;

	/**
	 * The View which displays the front image.
	 */
	private ImageView slideImageView;


	/**
	 * The View which displays the text.
	 */
	private TextView slideTitleView;

	private TextView slideDescriptionView;

	private String titleText = null;

	private String descriptionText = null;

	private int slideImageIndex = -1;

	/**
	 * @return a new ParallaxPage instance
	 */
	public static ParallaxPages newInstance() {
		return new ParallaxPages();
	}

	/**
	 * Constructs a new ParallaxPage instance. This is an empty public constructor, as required by
	 * the Android fragment framework.
	 */
	public ParallaxPages() {
		super();
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			final Bundle savedInstanceState) {
		rootView =
				(FrameLayout) inflater.inflate(R.layout.fragment_parallax_pages, container, false);
		slideImageView = (ImageView) rootView.findViewById(R.id.slide_image);
		slideTitleView = (TextView) rootView.findViewById(R.id.slide_title);
		slideDescriptionView = (TextView) rootView.findViewById(R.id.slide_description);
		Log.d("SLIDEINDEX",String.valueOf(this.slideImageIndex));
		reflectParametersInView();
		return rootView;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

//	/**
//	 * Updates the UI of this ParallaxPage to reflect the current member variables
//	 */
	private void reflectParametersInView() {

		if(slideImageView != null) this.setSlideImage(this.slideImageIndex);

		if (slideTitleView != null) {
			// The image will not update unless it is first reset by supplying null
			slideTitleView.setText(titleText);
		}

		if (slideDescriptionView != null) {
			// The text will not update unless it is first reset by supplying null
			slideDescriptionView.setText(descriptionText);
		}
	}

	public void setSlideImage(final int index) {
		switch (index) {
			case 0:
				slideImageView.setBackgroundResource(R.drawable.unlock_slide);
				break;
			case 1:
				slideImageView.setBackgroundResource(R.drawable.global_slide);
				break;
			case 2:
				slideImageView.setBackgroundResource(R.drawable.unlimited_slide);
				break;
			case 3:
				slideImageView.setBackgroundResource(R.drawable.turbo_slide);
				break;
		}
	}

	public void setSlideIndex(final int index) {
		this.slideImageIndex = index;
		reflectParametersInView();
	}


	public void setSlideTitle(final String title) {
		this.titleText = title;
		reflectParametersInView();
	}

	public void setSlideDescription(final String description) {
		this.descriptionText = description;
		reflectParametersInView();
	}

}