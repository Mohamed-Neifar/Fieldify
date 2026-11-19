package com.example.fieldify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fieldify.adapter.ReservationAdapter;
import com.example.fieldify.data.MockDataRepository;
import com.example.fieldify.model.Reservation;
import com.example.fieldify.network.RetrofitClient;
import com.example.fieldify.network.model.ReservationDto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgendaFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView rvAgendaMatches;
    private TextView tvEmpty;
    private ReservationAdapter reservationAdapter;

    private String selectedDateFormatted = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_agenda, container, false);

        initViews(view);
        setupCalendar();
        setupRecyclerView();

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        filterReservationsByDate(selectedDateFormatted);
    }

    private void initViews(View view) {
        calendarView = view.findViewById(R.id.calendar_view);
        rvAgendaMatches = view.findViewById(R.id.rv_agenda_matches);
        tvEmpty = view.findViewById(R.id.tv_empty_agenda);
    }

    private void setupCalendar() {
        Calendar cal = Calendar.getInstance();
        selectedDateFormatted = String.format(Locale.US, "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateFormatted = String.format(Locale.US, "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filterReservationsByDate(selectedDateFormatted);
        });
    }

    private void setupRecyclerView() {
        rvAgendaMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void filterReservationsByDate(String date) {
        RetrofitClient.getFieldifyApiService(requireContext()).getMyReservations().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationDto>> call, @NonNull Response<List<ReservationDto>> response) {
                List<Reservation> list = new ArrayList<>();
                if (response.isSuccessful() && response.body() != null) {
                    for (ReservationDto dto : response.body()) {
                        Reservation r = MyMatchesFragment.mapReservationDto(dto);
                        if (isMatchForDateAndStatus(r, date)) {
                            list.add(r);
                        }
                    }
                } else {
                    list = getLocalFilteredReservations(date);
                }
                bindAgendaList(list);
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationDto>> call, @NonNull Throwable t) {
                bindAgendaList(getLocalFilteredReservations(date));
            }
        });
    }

    private List<Reservation> getLocalFilteredReservations(String date) {
        List<Reservation> allReservations = MockDataRepository.getInstance().getReservations();
        List<Reservation> filteredList = new ArrayList<>();
        for (Reservation r : allReservations) {
            if (isMatchForDateAndStatus(r, date)) {
                filteredList.add(r);
            }
        }
        return filteredList;
    }

    private boolean isMatchForDateAndStatus(Reservation r, String date) {
        if (r == null || r.getDate() == null) return false;
        boolean sameDate = date.equalsIgnoreCase(r.getDate());
        boolean validStatus = "Confirmé".equalsIgnoreCase(r.getStatus())
                || "CONFIRMED".equalsIgnoreCase(r.getStatus())
                || "En attente".equalsIgnoreCase(r.getStatus())
                || "PENDING".equalsIgnoreCase(r.getStatus());
        return sameDate && validStatus;
    }

    private void bindAgendaList(List<Reservation> filteredList) {
        if (!isAdded()) return;

        if (reservationAdapter == null) {
            reservationAdapter = new ReservationAdapter(requireContext(), filteredList, reservation ->
                Toast.makeText(requireContext(), "Invitation envoyée", Toast.LENGTH_SHORT).show()
            );
            rvAgendaMatches.setAdapter(reservationAdapter);
        } else {
            reservationAdapter.updateData(filteredList);
        }

        if (tvEmpty != null) {
            tvEmpty.setVisibility(filteredList.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}