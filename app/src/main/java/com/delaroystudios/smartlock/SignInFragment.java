/**
 * Copyright Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.delaroystudios.smartlock;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.credentials.Credential;
import com.delaroystudios.smartlock.R;

public class SignInFragment extends Fragment {

    private static final String TAG = "SignInFragment";
    private TextInputLayout mUsernameTextInputLayout;
    private EditText mUsernameEditText;
    private TextInputLayout mPasswordTextInputLayout;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private ProgressBar mSignInProgressBar;

    @Override
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        View view = layoutInflater.inflate(R.layout.fragment_sign_in, container, false);
        mUsernameTextInputLayout = view.findViewById(R.id.usernameTextInputLayout);
        mPasswordTextInputLayout = view.findViewById(R.id.passwordTextInputLayout);

        mUsernameEditText = view.findViewById(R.id.usernameEditText);
        mUsernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateUsernameLayouts(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        mPasswordEditText = view.findViewById(R.id.passwordEditText);
        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validatePasswordLayouts(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        mSignInButton = view.findViewById(R.id.signInButton);
        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                setSignEnabled(false);
                String username = mUsernameTextInputLayout.getEditText().getText().toString();
                String password = mPasswordTextInputLayout.getEditText().getText().toString();

                if (username.isEmpty() || password.isEmpty()){
                    Toast.makeText(getContext(), "Please fill all fields", Toast.LENGTH_SHORT).show();
                    mSignInProgressBar.setVisibility(ProgressBar.INVISIBLE);
                    setSignEnabled(true);
                    return;
                }
                //TODO 2 Initiate save of valid credential
                // ************ Replace with Smart Lock sign in *****************
                Credential credential = new Credential.Builder(username)
                        .setPassword(password)
                        .build();
                if (CodelabUtil.isValidCredential(credential)) {
                    ((MainActivity) getActivity()).saveCredential(credential);
                } else {
                    Log.d(TAG, "Credentials are invalid. Username or password are " +
                            "incorrect.");
                    Toast.makeText(view.getContext(), R.string.invalid_creds_toast_msg,
                            Toast.LENGTH_SHORT).show();
                    setSignEnabled(true);
                }
                // **************** End Smart Lock sign in ***********************
            }
        });

        Button clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mUsernameTextInputLayout.getEditText().setText("");
                mPasswordTextInputLayout.getEditText().setText("");
            }
        });

        mSignInProgressBar = view.findViewById(R.id.signInProgress);
        mSignInProgressBar.setVisibility(ProgressBar.INVISIBLE);

        return view;
    }

    public void onResume() {
        super.onResume();
        if (((MainActivity) getActivity()).isResolving() || ((MainActivity) getActivity()).isRequesting()) {
            setSignEnabled(false);
        } else {
            setSignEnabled(true);
        }
    }

    /**
     * Enable or disable Sign In form.
     *
     * @param enable Enable form when true, disable when false.
     */
    protected void setSignEnabled(boolean enable) {
        mSignInButton.setEnabled(enable);
        mUsernameEditText.setEnabled(enable);
        mPasswordEditText.setEnabled(enable);
        if (!enable) {
            mSignInProgressBar.setVisibility(ProgressBar.VISIBLE);
        } else {
            mSignInProgressBar.setVisibility(ProgressBar.INVISIBLE);
        }
    }

    /**
     * The following validation methods are only for the purpose of the code lab. In a production application
     * you should not indicate to the user when their username or password is incorrect until they submit.
     */
    private void validateUsernameLayouts(CharSequence charSequence) {
        if (!CodelabUtil.isValidUsernameSoFar(charSequence.toString())) {
            mUsernameTextInputLayout.setError(getString(R.string.invalid_username_error_msg));
        } else {
            mUsernameTextInputLayout.setError(null);
        }
    }

    private void validatePasswordLayouts(CharSequence charSequence) {
        String currentUsername = mUsernameEditText.getText().toString();
        if (!CodelabUtil.isValidPasswordSoFar(currentUsername, charSequence.toString())) {
            mPasswordTextInputLayout.setError("invalid password");
        } else {
            mPasswordTextInputLayout.setError(null);
        }
    }

}
