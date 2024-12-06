package tallerhilos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class CarreraAtletas {

    private static final int META = 10000;

    public static void main(String[] args) {
        List<Atleta> atletas = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            atletas.add(new Atleta("Atleta " + i, META));
        }

        List<Thread> hilos = new ArrayList<>();
        for (Atleta atleta : atletas) {
            Thread hilo = new Thread(atleta);
            hilos.add(hilo);
            hilo.start();
        }

        for (Thread hilo : hilos) {
            try {
                hilo.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Atleta ganador = atletas.stream()
                .min((a1, a2) -> Long.compare(a1.getTiempo(), a2.getTiempo()))
                .orElse(null);

        if (ganador != null) {
            System.out.println("\nEl ganador es: " + ganador.getNombre());
            System.out.println("Tiempo total: " + ganador.getTiempo() + " ms");
        }
    }
}

class Atleta implements Runnable {
    private final String nombre;
    private final int velocidad;
    private final int meta;
    private long tiempo;

    public Atleta(String nombre, int meta) {
        this.nombre = nombre;
        this.velocidad = new Random().nextInt(1000) + 1;
        this.meta = meta;
    }

    public String getNombre() {
        return nombre;
    }

    public long getTiempo() {
        return tiempo;
    }

    @Override
    public void run() {
        int distanciaRecorrida = 0;
        long inicio = System.currentTimeMillis();

        while (distanciaRecorrida < meta) {
            distanciaRecorrida += velocidad;
            System.out.println(nombre + " ha recorrido " + distanciaRecorrida + " kms.");

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        long fin = System.currentTimeMillis();
        tiempo = fin - inicio;
        System.out.println(nombre + " ha llegado a la meta en " + tiempo + " ms.");
    }
}
