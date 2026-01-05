package services;
import entities.Account;
import entities.enums.AccountType;
import entities.enums.TransactionType;
import entities.Transaction;
import java.math.BigDecimal;

public class ComplianceService {
    private final BigDecimal LIMITE_AHORRO = new BigDecimal("2000.00");
    private final BigDecimal LIMITE_CORRIENTE = new BigDecimal("10000.00");

    public boolean verifyFundsOrigin(Transaction tx, Account origen, Account destino) {
        System.out.println("\n[Compliance] --- Iniciando Análisis de Origen ---");

        BigDecimal cantidad = tx.getAmount();

        if (origen == null) {
            System.out.println("[Compliance] -> ERROR CRÍTICO: Transacción sin cuenta de origen.");
            return false;
        }

        System.out.println("[Compliance] Origen: " + origen.getType() + " | Cantidad: " + cantidad);

        if (origen.getType() == AccountType.SAVINGS) {
            if (cantidad.compareTo(LIMITE_AHORRO) > 0) {
                System.out.println("[Compliance] -> ALERTA: Movimiento excesivo desde cuenta de AHORRO.");
                return false;
            }
        }

        if (origen.getType() == AccountType.CURRENT) {
            if (cantidad.compareTo(LIMITE_CORRIENTE) > 0) {
                System.out.println("[Compliance] -> ALERTA: La operación supera el límite.");
                return false;
            }
        }

        if (tx.getTransactionType() == TransactionType.TRANSFER && destino != null) {
            System.out.println("[Compliance] Destino: " + destino.getType());
            if (origen.getType() == AccountType.CURRENT && destino.getType() == AccountType.SAVINGS) {
                System.out.println("[Compliance] -> AVISO: Transferencia a cuenta de AHORRO externa bajo revisión manual.");
            }
        }

        System.out.println("[Compliance] -> Verificación Exitosa. Operación limpia.");
        return true;
    }

    public boolean detectFraud(Transaction tx) {
        System.out.println("\n[Compliance] --- Iniciando Análisis de Fraude ---");
        System.out.println("             Cantidad: " + tx.getAmount());
        System.out.println("             Tipo:     " + tx.getTransactionType());
        System.out.println("             Hora:     " + tx.getDate().toLocalTime());

        BigDecimal limiteInferiorSospecha = new BigDecimal("9000.00");
        BigDecimal limiteLegal = new BigDecimal("10000.00");

        if (tx.getAmount().compareTo(limiteInferiorSospecha) >= 0 &&
                tx.getAmount().compareTo(limiteLegal) < 0) {

            System.out.println("[Compliance] -> ALERTA: Rango sospechoso. Justo debajo de limite legal.");
            return true;
        }

        int hora = tx.getDate().getHour();
        boolean esMadrugada = (hora >= 2 && hora <= 5);
        boolean esCantidadConsiderable = tx.getAmount().compareTo(new BigDecimal("1000.00")) > 0;

        if (esMadrugada && esCantidadConsiderable) {
            System.out.println("[Compliance] -> ALERTA: Operación significativa en horario inusual (02:00 - 05:00).");
            return true;
        }

        if (tx.getTransactionType() == TransactionType.TRANSFER) {
            if (tx.getMainAccount() != 0 && tx.getTransferToAccount() != 0) {

                long idOrigen = tx.getMainAccount();
                long idDestino = tx.getTransferToAccount();

                if (idOrigen == idDestino) {
                    System.out.println("[Compliance] -> ERROR: Origen y Destino son la misma cuenta.");
                    return true;
                }
            }
        }

        if (tx.getAmount().compareTo(limiteLegal) > 0) {
            System.out.println("[Compliance] -> ALERTA AUTOMÁTICA: Cantidad excede el límite permitido.");
            return true;
        }
        System.out.println("[Compliance] -> Análisis finalizado. Transacción limpia.");
        return false;
    }
}