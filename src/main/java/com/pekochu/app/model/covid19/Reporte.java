package com.pekochu.app.model.covid19;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
@Table(name = "covid_reportes")
public class Reporte {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public Reporte(){ /* Empty constructor */ }

    public Reporte(Long id, String fecha, Long sospechosos,
                   Long confirmados, Long defunciones, Long recuperados, Long activos) {
        this.id = id;
        // this.stateId = stateId;
        this.fecha = fecha;
        this.sospechosos = sospechosos;
        this.confirmados = confirmados;
        this.defunciones = defunciones;
        this.recuperados = recuperados;
        this.activos = activos;
    }

    // private Long stateId;

    @Column(name = "fecha")
    private String fecha;

    @Column(name = "sospechosos")
    private Long sospechosos;

    @Column(name = "confirmados")
    private Long confirmados;

    @Column(name = "defunciones")
    private Long defunciones;

    @Column(name = "recuperados")
    private Long recuperados;

    @Column(name = "activos")
    private Long activos;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "estado_id")
    private Estado estado;

    @JsonIgnore
    public Long getId() {
        return id;
    }

    /*
    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }*/

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public Long getSospechosos() {
        return sospechosos;
    }

    public void setSospechosos(Long sospechosos) {
        this.sospechosos = sospechosos;
    }

    public Long getConfirmados() {
        return confirmados;
    }

    public void setConfirmados(Long confirmados) {
        this.confirmados = confirmados;
    }

    public Long getDefunciones() {
        return defunciones;
    }

    public void setDefunciones(Long defunciones) {
        this.defunciones = defunciones;
    }

    public Long getRecuperados() {
        return recuperados;
    }

    public void setRecuperados(Long recuperados) {
        this.recuperados = recuperados;
    }

    public Long getActivos() {
        return activos;
    }

    public void setActivos(Long activos) {
        this.activos = activos;
    }

    @JsonIgnore
    public Estado getState() {
        return estado;
    }

    public void setState(Estado estado) {
        this.estado = estado;
    }

}
