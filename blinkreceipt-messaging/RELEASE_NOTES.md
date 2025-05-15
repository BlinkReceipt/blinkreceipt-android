# Release Notes

## 1.0.0
- Initial release
- New Feature (Beta): Background Refresh and blinkreceipt-messaging SDK -- We’re excited to announce
  the beta release of Background Refresh for BlinkReceipt!
- Background Refresh allows your app to automatically fetch new orders and perform remote scrapes
  without requiring an active user session. Once enabled, BlinkReceipt sends periodic FCM push
  notifications to trigger order fetching and account updates seamlessly in the background.
- To enable this functionality, apps must now integrate the new blinkreceipt-messaging SDK, which
  handles push notifications and background actions.
- Key Highlights:
    - Automatic background order fetching for linked accounts (Account Linking and IMAP/Remote
      Scrape).
    - New blinkreceipt-messaging SDK dependency to manage push messaging and background workflows.
    - Lightweight integration: Initialize the SDK via code or AndroidX Startup, and delegate FCM
      messages to the SDK.
    - Supports full lifecycle management: Collect background fetch results via a simple SharedFlow
      API.
- Getting Started:
    - Add blinkreceipt-messaging to your app’s dependencies.
    - Integrate Firebase Cloud Messaging (FCM) support if not already present.
    - Forward incoming FCM push notifications to the Messaging SDK.

## 1.0.1
- Stability fixes and improvements