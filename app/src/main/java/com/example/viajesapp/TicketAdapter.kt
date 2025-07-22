package com.example.viajesapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TicketAdapter(private val tickets: List<TicketEntity>) :
    RecyclerView.Adapter<TicketAdapter.TicketViewHolder>() {

    inner class TicketViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvRuta: TextView = itemView.findViewById(R.id.tvRuta)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvSincronizado: TextView = itemView.findViewById(R.id.tvSincronizado)
        val tvFechaHora: TextView = itemView.findViewById(R.id.tvFechaHora)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TicketViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ticket, parent, false)
        return TicketViewHolder(view)
    }

    override fun onBindViewHolder(holder: TicketViewHolder, position: Int) {
        val ticket = tickets[position]
        holder.tvRuta.text = ticket.ruta
        holder.tvPrecio.text = "Precio: $${ticket.precio}"
        holder.tvSincronizado.text = if (ticket.sincronizado) "✅ Sincronizado" else "❌ No sincronizado"
        holder.tvFechaHora.text = "${ticket.fecha} ${ticket.hora}"
    }

    override fun getItemCount(): Int = tickets.size
}
