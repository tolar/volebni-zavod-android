package cz.vaclavtolar.volebnizavod.activity

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import cz.vaclavtolar.volebnizavod.R
import cz.vaclavtolar.volebnizavod.dto.Strana
import cz.vaclavtolar.volebnizavod.util.Constants
import cz.vaclavtolar.volebnizavod.util.Party
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols

open class ElectionActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    protected var id: Int? = null
    protected var name: String? = null
    protected var year: Int? = null

    companion object {
        val formatter: DecimalFormat
        var partiesMap: Map<Int, Party>? = null

        init {
            val symbols = DecimalFormatSymbols()
            symbols.groupingSeparator = ' '
            formatter = DecimalFormat("0.00", symbols)
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        var intent: Intent? = null
        when (item.itemId) {
            R.id.nav_votes -> {
                intent = Intent(this, VotesActivity::class.java).apply {
                    putExtra(Constants.ELECTION_ID, id.toString())
                    putExtra(Constants.ELECTION_NAME, name)
                    putExtra(Constants.ELECTION_YEAR, year)
                }}
            R.id.nav_mandates -> {
                intent = Intent(this, MandatesActivity::class.java).apply {
                    putExtra(Constants.ELECTION_ID, id.toString())
                    putExtra(Constants.ELECTION_NAME, name)
                    putExtra(Constants.ELECTION_YEAR, year)
                }}
        }
        if (intent != null) {
            startActivity(intent)
        }
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        drawer.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        val drawer = findViewById<DrawerLayout>(R.id.drawer_layout)
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    class StripeView(
        context: Context,
        attributeSet: AttributeSet? = null
    ) : View(context, attributeSet) {

        var max: Double? = null
        var value: Double? = null
        var strana: Strana? = null
        var partiesMap: Map<Int, Party>? = null

        private var viewHeight: Int = 0
        private var viewWidth: Int = 0

        override fun onDraw(canvas: Canvas?) {
            super.onDraw(canvas)
            val paint = Paint()
            paint.setStyle(Paint.Style.FILL);

            val party: Party? = partiesMap?.get(strana?.kstrana)
            party?.color?.let { paint.setColor(it) }
            val stripeWidth = (value?.div(max!!))?.times(
                viewWidth
            )
            canvas?.drawRect(0F, 0F, stripeWidth?.toFloat()!!, viewHeight.toFloat(), paint)
        }

        override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
            super.onSizeChanged(w, h, oldw, oldh)
            this.viewWidth = w
            this.viewHeight = h
        }
    }

}
