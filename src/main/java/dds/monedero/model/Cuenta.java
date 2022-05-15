package dds.monedero.model;

import dds.monedero.exceptions.MaximaCantidadDepositosException;
import dds.monedero.exceptions.MaximoExtraccionDiarioException;
import dds.monedero.exceptions.MontoNegativoException;
import dds.monedero.exceptions.SaldoMenorException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cuenta {

  private double saldo;
  private List<Movimiento> movimientos = new ArrayList<>();

  public Cuenta() {
    saldo = 0;
  }

  public Cuenta(double montoInicial) {
    saldo = montoInicial;
  }

  public void setMovimientos(List<Movimiento> movimientos) {
    this.movimientos = movimientos;
  }

  public void poner(double monto) {
    validarPoner(monto);
    new Deposito(LocalDate.now(), monto).agregateA(this);
  }

  public void validarMontoPositivo(double monto){
    if (monto <= 0) {
      throw new MontoNegativoException(monto + ": el monto a ingresar debe ser un valor positivo");
    }
  }

  public void validarPoner(double monto) {
    validarMontoPositivo(monto);
    int depositosDiarios = 3;
    if (this.getCantidadDepositos() >= depositosDiarios) {
      throw new MaximaCantidadDepositosException("Ya excedio los " + depositosDiarios + " depositos diarios");
    }
  }
  public void validarSacar(double monto) {
    validarMontoPositivo(monto);
    int limiteDiario = 1000;
    if (getSaldo() < monto) {
      throw new SaldoMenorException("No puede sacar mas de " + getSaldo() + " $");
    }
    double montoExtraidoHoy = getMontoExtraidoA(LocalDate.now());
    double limiteActual = limiteDiario - montoExtraidoHoy;
    if (monto > limiteActual) {
      throw new MaximoExtraccionDiarioException("No puede extraer mas de $ " + limiteDiario
          + " diarios, límite actual es: " + limiteActual);
    }
  }

  public int getCantidadDepositos(){
    return (int) this.getMovimientos().stream().filter(movimiento -> movimiento.isDeposito()).count();
  }
  public void sacar(double monto) {
    validarSacar(monto);
    new Extraccion(LocalDate.now(), monto).agregateA(this);
  }

  public void agregarMovimiento(Movimiento movimiento) {
    movimientos.add(movimiento);
  }

  public double getMontoExtraidoA(LocalDate fecha) {
    return getMovimientos().stream()
        .filter(movimiento -> movimiento.esDeLaFecha(fecha))
        .mapToDouble(Movimiento::getMonto)
        .sum();
  }

  public List<Movimiento> getMovimientos() {
    return movimientos;
  }

  public double getSaldo() {
    return saldo;
  }

  public void setSaldo(double saldo) {
    this.saldo = saldo;
  }

}
