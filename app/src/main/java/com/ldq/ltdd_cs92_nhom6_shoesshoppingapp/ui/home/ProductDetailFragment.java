package com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.ui.home;

import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.MainActivity;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.R;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.adapter.PhotoAdapter;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.databinding.FragmentProductDetailBinding;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.model.Cart;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.model.Photo;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.model.Product;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.ultil.SOService;
import com.ldq.ltdd_cs92_nhom6_shoesshoppingapp.ultil.Server;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductDetailFragment extends Fragment {
    private SOService soService;
    private AutoCompleteTextView autoCompleteTextView;
    private ViewPager viewPager;
    private PhotoAdapter photoAdapter;
    private FragmentProductDetailBinding binding;
    private Product product;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProductDetailBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        soService = Server.getSOService();
        Bundle bundle = this.getArguments();

        if(bundle != null){
            int productID = Integer.parseInt(bundle.getString("productID"));
            getProductDetail(productID);
        }
        // load size
        autoCompleteTextView = binding.sizeProduct;
        String[] size = new String[] {"39", "40", "41", "42"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                root.getContext(), R.layout.drop_down_item, size);
        autoCompleteTextView.setAdapter(adapter);
        autoCompleteTextView.setText(autoCompleteTextView.getAdapter().getItem(0).toString(), false);

        // s??? ki???n click ch???n item c???a dropdown size
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            }
        });

        // s??? ki???n click n??t th??m v??o gi???
        binding.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = Integer.parseInt(binding.sizeProduct.getText().toString());
                double price = product.getPromotionalPrice();
                int id = product.getId();
                if(MainActivity.carts.size() > 0){
                    boolean exists = false;
                    for(int i = 0; i < MainActivity.carts.size(); i++){
                        Cart cart = MainActivity.carts.get(i);
                        if(cart.getProductID() == id && cart.getSize() == size){
                            // n???u s???n ph???m n??y ???? c?? trong gi??? h??ng r???i
                            // set l???i s??? l?????ng t??ng 1 v?? t??nh l???i t???ng ti???n totalAmount
                            cart.setQuantity(cart.getQuantity()+1);
                            if(cart.getQuantity() >= 10){
                                cart.setQuantity(10);
                                String text = getResources().getString(R.string.minimum_limit_dialog);
                                Toast.makeText(getContext(), text, Toast.LENGTH_SHORT).show();
                            }
                            cart.setTotalAmount(price * cart.getQuantity());
                            exists = true;
                        }
                    }
                    if(exists == false){
                        // n???u s???n ph???m n??y ch??a ???????c th??m v??o gi??? h??ng
                        double totalAmount = price;
                        MainActivity.carts.add(new Cart(id, product.getName(),
                                product.getImage1(), size, 1, price, totalAmount));
                    }
                } else {
                    // th??m 1 s???n ph???m v??o gi??? h??ng khi gi??? h??ng ch??a c?? s???n ph???m n??o
                    double totalAmount = price;
                    MainActivity.carts.add(new Cart(id, product.getName(),
                            product.getImage1(), size, 1, price, totalAmount));
                }
                // hi???n s??? l?????ng s???n ph???m tr??n gi??? h??ng
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.showNumberProductInCart();
            }
        });
        return root;
    }

    private void getProductDetail(int productID) {
        soService.getProductByID(productID).enqueue(new Callback<Product>() {
            @Override
            public void onResponse(Call<Product> call, Response<Product> response) {
                if (response.isSuccessful()) {
                    product = response.body();
                    binding.nameProductDetail.setText(product.getName());
                    binding.descriptionProduct.setText(product.getDescription());
                    binding.priceProduct.setText(String.format("%,.0f???", product.getPrice()));
                    binding.priceProduct.setPaintFlags(binding.priceProduct.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    binding.promotionalPriceProduct.setText(String.format("%,.0f???", product.getPromotionalPrice()));
                    binding.perRedProduct.setText(String.format("-%d%%", product.getPerRed()));
                    binding.nameBrandProductDetail.setText(product.getBrand());
                    List<Photo> listPhoto = new ArrayList<>();
                    listPhoto.add(new Photo(product.getImage1()));
                    listPhoto.add(new Photo(product.getImage2()));
                    listPhoto.add(new Photo(product.getImage3()));
                    photoAdapter = new PhotoAdapter(getContext(), listPhoto);
                    viewPager = binding.viewpager;
                    viewPager.setAdapter(photoAdapter);
                } else {
                    Log.d("MainActivity", "fail load API Get Product Detail");
                }
            }

            @Override
            public void onFailure(Call<Product> call, Throwable t) {
                Log.d("MainActivity", "error loading from API Get Product Detail");
            }

        });
    }
}
