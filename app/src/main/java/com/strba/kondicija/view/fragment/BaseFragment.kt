import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.strba.kondicija.R

abstract class BaseFragment : Fragment() {
    private var fab: FloatingActionButton? = null
    private val handler = Handler(Looper.getMainLooper())
    private val hideFabRunnable = Runnable {
        activity?.runOnUiThread {
            fab?.visibility = View.GONE
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(getLayoutId(), container, false)
        fab = view.findViewById(R.id.fab)
        view.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    showFab()
                    true
                }
                MotionEvent.ACTION_UP -> {
                    hideFab()
                    v.performClick()
                    true
                }
                else -> false
            }
        }
        return view
    }

    private fun hideFab(){
        fab?.visibility = View.GONE
        handler.removeCallbacks(hideFabRunnable)
        handler.postDelayed(hideFabRunnable, 5000)
    }

    private fun showFab() {
        fab?.visibility = View.VISIBLE
        handler.removeCallbacks(hideFabRunnable)
        handler.postDelayed(hideFabRunnable, 5000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacks(hideFabRunnable)
    }

    abstract fun getLayoutId(): Int
}