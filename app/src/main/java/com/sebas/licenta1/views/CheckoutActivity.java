package com.sebas.licenta1.views;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.sebas.licenta1.R;
import com.sebas.licenta1.entities.PlaceDetails;
import com.sebas.licenta1.entities.Reservation;
import com.sebas.licenta1.utils.LoadingDialog;
import com.sebas.licenta1.utils.NumberPickerDialog;
import com.sebas.licenta1.viewmodels.UserViewModel;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class CheckoutActivity extends AppCompatActivity implements NumberPicker.OnValueChangeListener {
    private TextView datePick;
    private TextView timePick;
    private TextView seatsPick;
    private PlaceDetails placeDetails;
    private Reservation reservation;
    private NumberPickerDialog numberPickerDialog;
    private LoadingDialog loadingDialog;
    private LocalDate localDate;
    private LocalTime localTime;
    private UserViewModel userVm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);


        userVm = ViewModelProviders.of(this).get(UserViewModel.class);

        getIntentData();
        defineUI();
        createListeners();
    }

    @Override
    public void onValueChange(NumberPicker numberPicker, int i, int i1) {
        numberPickerDialog.dismiss();
        reservation.setSeatsNo(numberPicker.getValue());
        renderSeats();
    }

    private void defineUI() {
        datePick = findViewById(R.id.date_pick);
        timePick = findViewById(R.id.time_pick);
        seatsPick = findViewById(R.id.seats_pick);
        loadingDialog = new LoadingDialog(this);
        setDefaultValues();
    }

    private void setDefaultValues() {
        reservation = new Reservation();

        localDate = LocalDate.now();
        localTime = LocalTime.now();

        reservation.setLocalDateTime(LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        reservation.setSeatsNo(1);
        reservation.setPlaceID(placeDetails.getPlaceID());

        renderDate();
        renderTime();

        renderSeats();

        ((TextView) findViewById(R.id.name)).setText(placeDetails.getName());
        ((TextView) findViewById(R.id.preBooking)).setText(placeDetails.getPreBooking());
    }

    private void renderDate() {
        datePick.setText(reservation.extractLocalDateTime().format(DateTimeFormatter.ofPattern("dd MMM")));
    }

    private void renderTime() {
        timePick.setText(reservation.extractLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")));
    }

    private void renderSeats() {
        String renderedSeats = reservation.getSeatsNo() + " seat";

        if(reservation.getSeatsNo() > 1) {
            renderedSeats += "s";
        }

        seatsPick.setText(renderedSeats);
    }

    private void createListeners() {
        ImageView backButton =  findViewById(R.id.backIcon);
        Button checkoutButton = findViewById(R.id.confirm_button);
        TextView datePick = findViewById(R.id.date_pick);
        TextView timePick = findViewById(R.id.time_pick);
        TextView seatsPick = findViewById(R.id.seats_pick);

        backButton.setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                finish();
            }
        });

        checkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createReservation();
            }
        });

        datePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDatePicker();
            }
        });

        timePick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayTimePicker();
            }
        });

        seatsPick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displaySeatsPicker();
            }
        });
    }

    private void createReservation() {
        loadingDialog.show();
        userVm.addReservation(reservation)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.METHOD, "createReservation");
                        FirebaseAnalytics.getInstance(getApplicationContext()).logEvent("order_created", bundle);
                        goToSuccessfulScreen();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                        loadingDialog.dismiss();
                    }
                });
    }

    private void goToSuccessfulScreen() {
        Intent intent = new Intent(this, SuccessfulOrder.class);
        finish();
        startActivity(intent);
    }

    private void displaySeatsPicker() {
        numberPickerDialog = new NumberPickerDialog(this, 0, 10, reservation.getSeatsNo());
        numberPickerDialog.setValueChangeListener(this);
        numberPickerDialog.show();
    }

    private void setLocalDateTime() {
        reservation.setLocalDateTime(LocalDateTime.of(localDate, localTime).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
    }

    private void displayDatePicker() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(CheckoutActivity.this, R.style.PickerDialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                localDate = LocalDate.of(year, month + 1, dayOfMonth);
                setLocalDateTime();
                renderDate();
            }
        }, localDate.getYear(), localDate.getMonthValue() - 1, localDate.getDayOfMonth());

        datePickerDialog.show();
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
        datePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
        datePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));
    }

    private void displayTimePicker() {
        final TimePickerDialog timePickerDialog = new TimePickerDialog(CheckoutActivity.this, R.style.PickerDialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                if(!isValidTime(hourOfDay, minute)) {
                    Toast.makeText(CheckoutActivity.this, "Please set a future time",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                localTime = LocalTime.of(hourOfDay, minute);
                setLocalDateTime();
                renderTime();

            }
        }, localTime.getHour(), localTime.getMinute(), true);

        timePickerDialog.show();
        timePickerDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getColor(R.color.colorAccent));
        timePickerDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getColor(R.color.colorAccent));
    }

    private Boolean isValidTime(int hourOfDay, int minute) {
        LocalDateTime ldt = LocalDateTime.of(localDate, LocalTime.of(hourOfDay, minute));
        if(ldt.isAfter(LocalDateTime.now())) {
            return true;
        }
        return false;
    }

    private void getIntentData() {
        placeDetails = (PlaceDetails) getIntent().getSerializableExtra("placeDetails");
    }
}
