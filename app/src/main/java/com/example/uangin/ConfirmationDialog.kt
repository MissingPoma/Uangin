import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment

class ConfirmationDialog : DialogFragment() {

    // Interface for communication with the calling fragment/activity
    interface ConfirmationListener {
        fun onConfirmReset()
    }

    private lateinit var listener: ConfirmationListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        try {
            listener = context as ConfirmationListener
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement ConfirmationListener")
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setMessage("Apakah Anda yakin ingin menghapus semua data?")
                .setPositiveButton("Ya") { dialog, id ->
                    listener.onConfirmReset()
                }
                .setNegativeButton("Tidak") { dialog, id ->
                    // User cancelled the dialog
                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
