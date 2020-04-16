package com.arabdevelopers.shamelapp.activities_fragments.activity_login;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.arabdevelopers.shamelapp.R;
import com.arabdevelopers.shamelapp.activities_fragments.activity_home.HomeActivity;
import com.arabdevelopers.shamelapp.activities_fragments.activity_verification_code.VerificationCodeActivity;
import com.arabdevelopers.shamelapp.databinding.ActivityLoginBinding;
import com.arabdevelopers.shamelapp.interfaces.Listeners;
import com.arabdevelopers.shamelapp.language.Language;
import com.arabdevelopers.shamelapp.models.LoginModel;
import com.arabdevelopers.shamelapp.share.Common;

import io.paperdb.Paper;

public class LoginActivity extends AppCompatActivity implements Listeners.LoginListener {
    private ActivityLoginBinding binding;
    private LoginModel loginModel;

    @Override
    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "ar")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login);
        initView();
    }

    private void initView() {
        loginModel = new LoginModel();
        binding.setLoginModel(loginModel);
        binding.setListener(this);
        binding.edtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().startsWith("0"))
                {
                    binding.edtPhone.setText("");
                }
            }
        });
    }

    @Override
    public void skip() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void validate() {
        if (loginModel.isDataValid(this))
        {
            Common.CloseKeyBoard(this,binding.edtPhone);
            navigateToVerificationCodeActivity();
        }
    }

    private void navigateToVerificationCodeActivity() {

        Intent intent = new Intent(this, VerificationCodeActivity.class);
        intent.putExtra("phone_code","+965");
        intent.putExtra("phone",loginModel.getPhone());
        startActivity(intent);
        finish();

    }
}
