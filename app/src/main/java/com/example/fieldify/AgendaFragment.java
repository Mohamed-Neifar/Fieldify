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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class AgendaFragment extends Fragment {

    private CalendarView calendarView;
    private RecyclerView rvAgendaMatches;
    private TextView tvEmpty;
    private ReservationAdapter reservationAdapter;

    private String selectedDateFormatted = "2026-11-24";

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
        selectedDateFormatted = String.format(Locale.getDefault(), "%04d-%02d-%02d",
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            selectedDateFormatted = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
            filterReservationsByDate(selectedDateFormatted);
        });
    }

    private void setupRecyclerView() {
        rvAgendaMatches.setLayoutManager(new LinearLayoutManager(requireContext()));
    }

    private void filterReservationsByDate(String date) {
        List<Reservation> allReservations = MockDataRepository.getInstance().getReservations();
        List<Reservation> filteredList = new ArrayList<>();

        for (Reservation r : allReservations) {
            if (date.equalsIgnoreCase(r.getDate()) || "2026-11-24".equalsIgnoreCase(r.getDate())) {
                filteredList.add(r);
            }
        }

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