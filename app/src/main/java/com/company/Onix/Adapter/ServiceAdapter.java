package com.company.Onix.Adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.company.Onix.Modelos.Service;
import com.company.Onix.R;

import java.util.ArrayList;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private ArrayList<Service> localDataSet;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView direccion_pasajero;
        private final TextView destino_pasajero;
        private final TextView kilometros_tarjeta_dos;
        private final TextView pasajero_paga;
        private final TextView textView_status;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            direccion_pasajero = (TextView) view.findViewById(R.id.direccion_pasajero);
            destino_pasajero = (TextView) view.findViewById(R.id.destino_pasajero);
            kilometros_tarjeta_dos = (TextView) view.findViewById(R.id.kilometros_tarjeta_dos);
            pasajero_paga = (TextView) view.findViewById(R.id.pasajero_paga);
            textView_status = (TextView) view.findViewById(R.id.textView_status);
        }

        public TextView getTextViewOrigen() {
            return direccion_pasajero;
        }

        public TextView getTextViewDestination() {
            return destino_pasajero;
        }

        public TextView getTextViewKm() {
            return kilometros_tarjeta_dos;
        }

        public TextView getTextViewValue() {
            return pasajero_paga;
        }

        public TextView getTextViewStatus() {
            return textView_status;
        }

        @Override
        public void onClick(View view) {

        }
    }

    /**
     * Initialize the dataset of the Adapter
     *
     * @param dataSet String[] containing the data to populate views to be used
     *                by RecyclerView
     */
    public void CustomAdapter(ArrayList<Service>dataSet) {
        localDataSet = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.tarjeta_normal, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextViewOrigen().setText(localDataSet.get(position).getDireccion());
        viewHolder.getTextViewDestination().setText(localDataSet.get(position).getDestino());
        viewHolder.getTextViewKm().setText(localDataSet.get(position).getKilometros());
        viewHolder.getTextViewValue().setText(localDataSet.get(position).getPrecio());
        viewHolder.getTextViewStatus().setText(localDataSet.get(position).getStatus());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return localDataSet.size();
    }
}

