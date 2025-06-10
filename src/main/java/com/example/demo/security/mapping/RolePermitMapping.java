package com.example.demo.security.mapping;

import com.example.demo.security.enums.Permit;
import com.example.demo.security.enums.Role;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class RolePermitMapping {

    private final Map<Role, Set<Permit>> rolePermissions = new EnumMap<>(Role.class);

    public RolePermitMapping() {
        initializeMappings();
    }

    private void initializeMappings() {
        // ADMIN - acceso total
        rolePermissions.put(Role.ROLE_ADMIN, EnumSet.allOf(Permit.class));

        // USER
            rolePermissions.put(Role.ROLE_USER, EnumSet.of(
                    // TRIP
                    Permit.CREAR_VIAJE,
                    Permit.VER_VIAJE,
                    Permit.MODIFICAR_VIAJE,
                    Permit.ELIMINAR_VIAJE,
                    Permit.OBTENER_RECOMENDACIONES_VIAJES,
                    Permit.OBTENER_RECOMENDACIONES_FILTRADAS,
                    Permit.VER_VIAJE_USUARIO,

                // ACTIVITY (usuario)
                Permit.CREAR_ACTIVIDAD_USUARIO,
                Permit.VER_ACTIVIDAD,
                Permit.VER_ACTIVIDAD_USUARIO,
                Permit.MODIFICAR_ACTIVIDADES_USUARIO,
                Permit.ELIMINAR_ACTIVIDAD_USUARIO,

                // CHECKLIST
                Permit.CREAR_CHECKLIST,
                Permit.MODIFICAR_CHECKLIST,
                Permit.VER_CHECKLIST,
                Permit.VER_CHECKLIST_USER,
                Permit.ELIMINAR_CHECKLIST,

                // CHECKLIST ITEM
                Permit.CREAR_CHECKLISTITEM,
                Permit.VER_CHECKLISTITEM,
                Permit.ELIMINAR_CHECKLISTITEM,
                Permit.MODIFICAR_CHECKLISTITEM,

                // EXPENSE
                Permit.CREAR_GASTO,
                Permit.VER_GASTO,
                Permit.MODIFICAR_GASTO,
                Permit.ELIMINAR_GASTO,
                Permit.VER_GASTO_USUARIO,
                Permit.VER_PROMEDIO_USUARIO,
                Permit.VER_GASTOS_VIAJES_TOTAL,
                Permit.VER_GASTOS_VIAJES_DIVIDIDO,
                Permit.VER_PROMEDIO_VIAJE,

                // ITINERARIO
                Permit.CREAR_ITINERARIO,
                Permit.VER_ITINERARIO_USUARIO,
                Permit.VER_ITINERARIO,
                Permit.MODIFICAR_ITINERARIO,
                Permit.ELIMINAR_ITINERARIO,

                // USER
                Permit.REGISTRARSE,
                Permit.INICIAR_SESION,
                Permit.CERRAR_SESION,
                Permit.MODIFICAR_USUARIO,
                Permit.VER_PERFIL,
                Permit.ELIMINAR_USUARIO,

                // RESERVATION
                Permit.CREAR_RESERVA,
                Permit.CANCELAR_RESERVA,
                Permit.VER_RESERVAS_USUARIO
        ));

        // COMPANY
        rolePermissions.put(Role.ROLE_COMPANY, EnumSet.of(
                // ACTIVITY (empresa)
                Permit.CREAR_ACTIVIDAD_EMPRESA,
                Permit.VER_ACTIVIDAD_EMPRESA,
                Permit.MODIFICAR_ACTIVIDADES_EMPRESA,
                Permit.ELIMINAR_ACTIVIDAD_EMPRESA,

                // USER (básicos)
                Permit.INICIAR_SESION,
                Permit.CERRAR_SESION,
                
                // EMPRESA
                Permit.VER_EMPRESA,
                Permit.MODIFICAR_EMPRESA,
                Permit.ELIMINAR_EMPRESA,
                Permit.VER_RESERVAS_EMPRESA
        ));
    }

    public Set<Permit> getPermitsForRole(Role role) {
        return rolePermissions.getOrDefault(role, EnumSet.noneOf(Permit.class));
    }
}

