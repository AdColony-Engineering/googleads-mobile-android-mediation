package com.google.ads.mediation.adcolony;

import android.view.View;

import androidx.annotation.NonNull;

import com.adcolony.sdk.AdColony;
import com.adcolony.sdk.AdColonyAdSize;
import com.adcolony.sdk.AdColonyAdView;
import com.adcolony.sdk.AdColonyAdViewListener;
import com.adcolony.sdk.AdColonyZone;

import com.google.android.gms.ads.mediation.MediationAdLoadCallback;
import com.google.android.gms.ads.mediation.MediationBannerAd;
import com.google.android.gms.ads.mediation.MediationBannerAdCallback;
import com.google.android.gms.ads.mediation.MediationBannerAdConfiguration;

import static com.google.ads.mediation.adcolony.AdColonyAdapterUtils.convertPixelsToDp;

public class AdColonyBannerRenderer extends AdColonyAdViewListener implements MediationBannerAd {

  private MediationBannerAdCallback mBannerAdCallback;
  private final MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> mAdLoadCallback;
  private AdColonyAdView adColonyAdView;
  private final MediationBannerAdConfiguration adConfiguration;

  public AdColonyBannerRenderer(
          MediationBannerAdConfiguration adConfiguration,
          MediationAdLoadCallback<MediationBannerAd, MediationBannerAdCallback> callback
  ) {
    this.mAdLoadCallback = callback;
    this.adConfiguration = adConfiguration;
  }

  public void render() {
    String requestedZone = adConfiguration.getServerParameters().getString(AdColonyAdapterUtils.KEY_ZONE_ID);
    // Setting the requested size as it is as adcolony view size.
    AdColonyAdSize adSize = new AdColonyAdSize(
            convertPixelsToDp(adConfiguration.getAdSize().getWidthInPixels(adConfiguration.getContext())),
            convertPixelsToDp(adConfiguration.getAdSize().getHeightInPixels(adConfiguration.getContext()))
    );

    if (adSize != null) {
      AdColony.requestAdView(requestedZone, this, adSize);
    } else {
      mAdLoadCallback.onFailure("Failed to request banner with unsupported size");
    }
  }

  @Override
  public void onRequestFilled(AdColonyAdView adColonyAdView) {
    this.adColonyAdView = adColonyAdView;
    this.mBannerAdCallback = mAdLoadCallback.onSuccess(this);
  }

  @Override
  public void onRequestNotFilled(AdColonyZone zone) {
    this.mAdLoadCallback.onFailure("Failed to load banner.");
  }

  @Override
  public void onLeftApplication(AdColonyAdView ad) {
    this.mBannerAdCallback.onAdLeftApplication();
  }

  @Override
  public void onOpened(AdColonyAdView ad) {
    this.mBannerAdCallback.onAdOpened();
  }

  @Override
  public void onClosed(AdColonyAdView ad) {
    this.mBannerAdCallback.onAdClosed();
  }

  @Override
  public void onClicked(AdColonyAdView ad) {
    this.mBannerAdCallback.reportAdClicked();
  }

  @NonNull
  @Override
  public View getView() {
    return this.adColonyAdView;
  }
}