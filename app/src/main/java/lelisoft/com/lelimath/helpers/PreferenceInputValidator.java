package lelisoft.com.lelimath.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.preference.EditTextPreference;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

public abstract class PreferenceInputValidator implements TextWatcher {
	protected final EditTextPreference preference;
	protected final EditText editText;

	public PreferenceInputValidator(EditTextPreference preference) {
		this.preference = preference;
		this.editText = preference.getEditText();
		editText.addTextChangedListener(this);
	}

	public abstract void isValid(CharSequence s) throws Exception;

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {}

	@Override
	public void afterTextChanged(Editable s) {
		boolean isValid = false;
		try {
			isValid(editText.getText());
			editText.setError(null);
			isValid = true;
		} catch (Exception ex) {
			editText.setError(ex.getMessage());
		} finally {
			if (preference.getDialog() instanceof AlertDialog) {
				final Button button = ((AlertDialog) preference.getDialog()).getButton(DialogInterface.BUTTON_POSITIVE);
				if (button != null) {
					button.setEnabled(isValid);
				}
			}
		}
	}
}