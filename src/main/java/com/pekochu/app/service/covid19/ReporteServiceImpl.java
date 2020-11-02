package com.pekochu.app.service.covid19;

import com.pekochu.app.model.covid19.Estado;
import com.pekochu.app.model.covid19.Reporte;
import com.pekochu.app.repository.ReporteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReporteServiceImpl implements ReporteService{

    @Autowired
    ReporteRepository reporteRepository;

    @Override
    public Reporte createReport(Reporte report) {
        if(!reporteRepository.existsById(String.valueOf(report.getId()))) {
            return reporteRepository.save(report);
        }else{
            return null;
        }
    }

    @Override
    public Reporte lastReport() {
        return reporteRepository.findTopByOrderByIdDesc();
    }

    @Override
    public List<Reporte> findReportsByState(Estado state) {
        return reporteRepository.findByEstadoOrderByFechaDesc(state);
    }

    @Override
    public List<Reporte> getAllNationalReports() {
        Estado state = new Estado();
        state.setId(33L);
        return reporteRepository.findByEstadoOrderByFechaDesc(state);
    }

    @Override
    public List<Reporte> findReportsByDate(String date) {
        return reporteRepository.findByFechaOrderByEstadoAsc(date);
    }

    @Override
    public List<Reporte> topStateReportsByDate(String date) {
        return reporteRepository.findByFechaOrderByConfirmadosDesc(date);
    }

    @Override
    public Reporte updateReport(Reporte report) {
        return reporteRepository.save(report);
    }
}
