package com.blurr.voice

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView

class ProPurchaseActivity : BaseNavigationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pro_purchase)

        val priceTextView = findViewById<TextView>(R.id.price_text)
        val purchaseButton = findViewById<Button>(R.id.purchase_button)
        val loadingProgressBar = findViewById<ProgressBar>(R.id.loading_progress)
        val backButton = findViewById<View>(R.id.back_button)

        priceTextView.text = "Subscription disabled in this build"
        purchaseButton.visibility = View.GONE
        loadingProgressBar.visibility = View.GONE
        backButton.setOnClickListener { finish() }
    }

    override fun getContentLayoutId(): Int = R.layout.activity_pro_purchase

    override fun getCurrentNavItem(): NavItem = BaseNavigationActivity.NavItem.SETTINGS
}