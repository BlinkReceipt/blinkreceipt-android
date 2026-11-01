package com.actualplatform.android.activation.development

import android.app.Application
import com.actualplatform.android.activation.development.ui.ActivationActivity

class ActivationApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Since Activation 1.3.0 the SDK has no default for the user's privacy answers and
        // ActivationClient.instance throws until initialize(privacy) has run, so the host states
        // them here, before anything reaches the client. This app stands in for a host and takes
        // them from Settings; a fresh install states unresolved answers with the SDK owning
        // Google's restricted-data-processing preference, so ads load restricted rather than not
        // at all.
        ActivationActivity.initializeActivationClient(this)
    }
}
