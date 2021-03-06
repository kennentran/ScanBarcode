package com.kennen.scanbarcode;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.text.NumberFormat;
import java.util.Locale;

import static com.kennen.scanbarcode.MainActivity.intentIntegrator;
import static com.kennen.scanbarcode.MainActivity.myDB;
import static com.kennen.scanbarcode.MainActivity.viewPager;

public class ScanFragment extends Fragment
{
    private View rootView;
    private Button scanButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        rootView = inflater.inflate(R.layout.scan_frag, container, false);
        scanButton = rootView.findViewById(R.id.btn_scan);
        intentIntegrator = IntentIntegrator.forSupportFragment(ScanFragment.this);
        scanButton.setOnClickListener(v ->
        {
            intentIntegrator.setPrompt("Đưa mã vào vạch đỏ");
            intentIntegrator.initiateScan();
        });
        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null)
        {
            if (result.getContents() == null)
            {
                Toast.makeText(getContext(), "Đã hủy quét mã", Toast.LENGTH_LONG).show();
            } else
            {
                Product product = myDB.getProduct(result.getContents());
                if(product == null)
                {
                    Toast.makeText(getContext(), "Chưa có trong cơ sở dữ liệu! Vui lòng cập nhật", Toast.LENGTH_LONG).show();
                    viewPager.setCurrentItem(1, true);
                    EditText temp = (EditText)viewPager.findViewById(R.id.et_code);
                    temp.setText(result.getContents().toString());
                }
                else
                {
                    String priceVNFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN")).format(Integer.valueOf(product.getPrice()));
                    Toast.makeText(getContext(), "Giá sản phẩm: " + priceVNFormat, Toast.LENGTH_LONG).show();
                }
            }
        } else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
