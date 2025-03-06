package com.example.pawpalnetwork.bd;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.example.pawpalnetwork.R;
import com.example.pawpalnetwork.ui.proveedor.Main_Proveedor;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class GestionBD {

    private FirebaseFirestore firestore;
    private FirebaseStorage storage;

    public GestionBD(FirebaseStorage storage, FirebaseFirestore firestore) {
        this.storage = storage;
        this.firestore = firestore;
    }

    public GestionBD() {
    }

    // Crear Usuario/Proveedor
    public void agregarUsuario(UsuarioGeneral usuario, Uri fotoUri, OnSuccessCallback onSuccess) {
        if (fotoUri == null || usuario == null) {
            Log.i("BD", "Error: URI o usuario es nulo.");
            return;
        }

        StorageReference storageRef = storage.getReference("fotos_perfil_usuarios/" + usuario.getId() + ".jpg");
        storageRef.putFile(fotoUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    usuario.setFotoPerfil(uri.toString());

                    firestore.collection("usuarios").document(usuario.getId().toString()).set(usuario)
                            .addOnSuccessListener(aVoid -> {
                                Log.i("BD", "Usuario agregado correctamente.");
                                if (onSuccess != null) onSuccess.onSuccess(); // Llama al método de la interfaz
                            })
                            .addOnFailureListener(e -> Log.e("BD", "Error al agregar usuario: " + e.getMessage()));
                }))
                .addOnFailureListener(e -> Log.e("BD", "Error al subir imagen a Firebase Storage: " + e.getMessage()));
    }

    // Editar Usuario/Proveedor (con opción de nueva foto)
    public void editarUsuario(UsuarioGeneral usuario, Uri nuevaFotoUri) {
        if (nuevaFotoUri != null) {
            StorageReference storageRef = storage.getReference("fotos_perfil_usuarios/" + usuario.getId() + ".jpg");
            storageRef.putFile(nuevaFotoUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        usuario.setFotoPerfil(uri.toString());
                        actualizarUsuarioEnFirestore(usuario);
                    }))
                    .addOnFailureListener(e -> System.err.println("Error al subir nueva foto a Firebase Storage: " + e.getMessage()));
        } else {
            actualizarUsuarioEnFirestore(usuario);
        }
    }

    private void actualizarUsuarioEnFirestore(UsuarioGeneral usuario) {
        firestore.collection("usuarios").document(usuario.getId().toString()).set(usuario)
                .addOnSuccessListener(aVoid -> System.out.println("Usuario actualizado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar usuario: " + e.getMessage()));
    }

    // Eliminar Usuario/Proveedor
    public void eliminarUsuario(String id,boolean esProveedor) {
        // Eliminar foto de perfil del usuario en Firebase Storage
        StorageReference storageRef = storage.getReference("fotos_perfil_usuarios/" + id + ".jpg");

        storageRef.delete()
                .addOnSuccessListener(aVoid -> {
                    // Eliminar el usuario de la colección 'usuarios'
                    firestore.collection("usuarios").document(id).delete()
                            .addOnSuccessListener(aVoid2 -> {
                                // Eliminar servicios del usuario
                                eliminarServicios(id);
                                // Eliminar contrataciones del usuario
                                eliminarContrataciones(id,esProveedor);
                                // Eliminar notificaciones del usuario
                                eliminarNotificaciones(id,esProveedor);
                                // Eliminar geolocalizaciones del usuario
                                eliminarGeolocalizaciones(id);

                                System.out.println("Usuario y datos asociados eliminados correctamente.");
                            })
                            .addOnFailureListener(e -> System.err.println("Error al eliminar usuario: " + e.getMessage()));
                })
                .addOnFailureListener(e -> System.err.println("Error al eliminar foto de Firebase Storage: " + e.getMessage()));
    }

    // Función para eliminar los servicios asociados al usuario
    private void eliminarServicios(String id) {
        firestore.collection("servicios")
                .whereEqualTo("usuarioId", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        firestore.collection("servicios").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> System.out.println("Servicio eliminado: " + document.getId()))
                                .addOnFailureListener(e -> System.err.println("Error al eliminar servicio: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error al obtener servicios del usuario: " + e.getMessage()));
    }

    // Función para eliminar las contrataciones asociadas al usuario
    private void eliminarContrataciones(String id, boolean esProveedor) {
        String campoBusqueda = esProveedor ? "idProveedor" : "idCliente";

        firestore.collection("contrataciones")
                .whereEqualTo(campoBusqueda, id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        firestore.collection("contrataciones").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> System.out.println("Contratación eliminada: " + document.getId()))
                                .addOnFailureListener(e -> System.err.println("Error al eliminar contratación: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error al obtener contrataciones del usuario: " + e.getMessage()));
    }


    // Función para eliminar las notificaciones asociadas al usuario
    private void eliminarNotificaciones(String id,boolean esProveedor) {
        String campoBusqueda = esProveedor ? "idProveedor" : "idUsuario";

        firestore.collection("notificaciones")
                .whereEqualTo(campoBusqueda, id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        firestore.collection("notificaciones").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> System.out.println("Notificación eliminada: " + document.getId()))
                                .addOnFailureListener(e -> System.err.println("Error al eliminar notificación: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error al obtener notificaciones del usuario: " + e.getMessage()));
    }

    // Función para eliminar las geolocalizaciones asociadas al usuario
    private void eliminarGeolocalizaciones(String id) {
        firestore.collection("geolocalizaciones")
                .whereEqualTo("userId", id)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot document : queryDocumentSnapshots) {
                        firestore.collection("geolocalizaciones").document(document.getId()).delete()
                                .addOnSuccessListener(aVoid -> System.out.println("Geolocalización eliminada: " + document.getId()))
                                .addOnFailureListener(e -> System.err.println("Error al eliminar geolocalización: " + e.getMessage()));
                    }
                })
                .addOnFailureListener(e -> System.err.println("Error al obtener geolocalizaciones del usuario: " + e.getMessage()));
    }


    // Listar todos los Usuarios y Proveedores
    public void listarUsuarios() {
        firestore.collection("usuarios").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            UsuarioGeneral usuario = document.toObject(UsuarioGeneral.class);
                            System.out.println(usuario); // Mostrar datos
                        });
                    } else {
                        System.err.println("Error al listar usuarios: " + task.getException().getMessage());
                    }
                });
    }

    // Obtener un Usuario o Proveedor por ID
    public void obtenerUsuarioPorId(String id, Consumer<UsuarioGeneral> onSuccess, Consumer<Exception> onFailure) {
        firestore.collection("usuarios").document(id).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            UsuarioGeneral usuario = document.toObject(UsuarioGeneral.class);
                            onSuccess.accept(usuario);
                        } else {
                            Log.e("UserRepository", "No se encontró el usuario con el ID especificado.");
                            onFailure.accept(new FirebaseFirestoreException("No se encontró el usuario con el ID especificado.", FirebaseFirestoreException.Code.NOT_FOUND));
                        }
                    } else {
                        Exception e = task.getException();
                        if (e != null) {
                            onFailure.accept(e);
                        } else {
                            Log.e("UserRepository", "Error desconocido al obtener el usuario.");
                            onFailure.accept(new Exception("Error desconocido al obtener el usuario."));
                        }
                    }
                });
    }



    /////Servicio Mascota

    public void agregarServicioMascota(ServicioMascota servicioMascota) {
        String id = UUID.randomUUID().toString();
        servicioMascota.setId(UUID.fromString(id));

        firestore.collection("servicioMascota").document(id).set(servicioMascota)
                .addOnSuccessListener(aVoid -> System.out.println("ServicioMascota agregado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al agregar ServicioMascota: " + e.getMessage()));
    }

    // Editar ServicioMascota
    public void editarServicioMascota(ServicioMascota servicioMascota) {
        firestore.collection("servicioMascota").document(servicioMascota.getId().toString()).set(servicioMascota)
                .addOnSuccessListener(aVoid -> System.out.println("ServicioMascota actualizado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar ServicioMascota: " + e.getMessage()));
    }

    // Eliminar ServicioMascota
    public void eliminarServicioMascota(String id) {
        firestore.collection("servicioMascota").document(id).delete()
                .addOnSuccessListener(aVoid -> System.out.println("ServicioMascota eliminado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al eliminar ServicioMascota: " + e.getMessage()));
    }

    // Listar todos los ServicioMascota
    public void listarServicioMascotas() {
        firestore.collection("servicioMascota").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            ServicioMascota servicioMascota = document.toObject(ServicioMascota.class);
                            System.out.println(servicioMascota); // Mostrar datos
                        });
                    } else {
                        System.err.println("Error al listar ServicioMascota: " + task.getException().getMessage());
                    }
                });
    }


    /////Servicio

    public void agregarServicio(Servicio servicio) {
        String id = UUID.randomUUID().toString();
        servicio.setId(id);

        firestore.collection("servicios").document(id).set(servicio)
                .addOnSuccessListener(aVoid -> System.out.println("Servicio agregado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al agregar Servicio: " + e.getMessage()));
    }

    // Editar Servicio
    public void editarServicio(Servicio servicio) {
        firestore.collection("servicios").document(servicio.getId()).set(servicio)
                .addOnSuccessListener(aVoid -> System.out.println("Servicio actualizado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar Servicio: " + e.getMessage()));
    }

    // Eliminar Servicio
    public void eliminarServicio(String id) {
        firestore.collection("servicios").document(id).delete()
                .addOnSuccessListener(aVoid -> System.out.println("Servicio eliminado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al eliminar Servicio: " + e.getMessage()));
    }

    // Listar Servicios
    public void listarServicios() {
        firestore.collection("servicios").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            Servicio servicio = document.toObject(Servicio.class);
                            System.out.println(servicio); // Mostrar datos
                        });
                    } else {
                        System.err.println("Error al listar Servicios: " + task.getException().getMessage());
                    }
                });
    }


    public void uploadImagesAndSaveService(ArrayList<Uri> photoList, Servicio servicio, UploadCallback callback) {
        List<String> imagenesRutas = new ArrayList<>();

        // Notificar que la subida ha comenzado
        if (callback != null) {
            callback.onUploadStart();
        }

        // Filtra las imágenes que ya existen
        for (Uri uri : photoList) {
            String uriString = uri.toString();
            if (uriString.startsWith("https://")) {
                imagenesRutas.add(uriString);
            }
        }

        // Subir imágenes nuevas
        for (Uri imageUri : photoList) {
            if (!imageUri.toString().startsWith("https://")) {
                String imageId = UUID.randomUUID().toString();
                StorageReference fileRef = storage.getReference().child("servicios/" + imageId);

                fileRef.putFile(imageUri)
                        .addOnSuccessListener(taskSnapshot -> fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                            imagenesRutas.add(uri.toString());

                            // Verificar si todas las imágenes están en `imagenesRutas`
                            if (imagenesRutas.size() == photoList.size()) {
                                servicio.setImagenesRutas(imagenesRutas);

                                if (servicio.getId() != null) {
                                    editarServicio(servicio);
                                } else {
                                    agregarServicio(servicio);
                                }

                                if (callback != null) {
                                    callback.onUploadSuccess();
                                }
                            }
                        }).addOnFailureListener(e -> {
                            if (callback != null) {
                                callback.onUploadFailure("Error al obtener URL de imagen: " + e.getMessage());
                            }
                        }))
                        .addOnFailureListener(e -> {
                            if (callback != null) {
                                callback.onUploadFailure("Error al subir imagen: " + e.getMessage());
                            }
                        });
            }
        }

        // Si no hay imágenes nuevas para subir
        if (imagenesRutas.size() == photoList.size()) {
            servicio.setImagenesRutas(imagenesRutas);
            if (servicio.getId() != null) {
                editarServicio(servicio);
            } else {
                agregarServicio(servicio);
            }

            if (callback != null) {
                callback.onUploadSuccess();
            }
        }
    }




    public int imagenServicio(String servicio) {
        Log.e("GestionBD", "El servicio es nulo"+servicio);
        switch (servicio) {
            case "Pasear":
                return R.drawable.pasear;
            case "Entrenamiento":
                return R.drawable.entrenamiento;
            case "Veterinario":
                return R.drawable.veterinario;
            case "Baño y Estetica":
                return R.drawable.banoestetica;
            case "DayCare":
                return R.drawable.daycare;
            default:
                return R.drawable.default_service; // Imagen por defecto si no coincide
        }
    }

    //// Mensaje

    // Crear Mensaje
    public void agregarMensaje(Mensaje mensaje) {
        String id = UUID.randomUUID().toString();
        mensaje.setId(id);

        firestore.collection("chats").document(mensaje.getChatId().toString())
                .collection("mensajes").document(id).set(mensaje)
                .addOnSuccessListener(aVoid -> System.out.println("Mensaje agregado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al agregar Mensaje: " + e.getMessage()));
    }

    // Editar Mensaje
    public void editarMensaje(Mensaje mensaje) {
        firestore.collection("chats").document(mensaje.getChatId().toString())
                .collection("mensajes").document(mensaje.getId().toString()).set(mensaje)
                .addOnSuccessListener(aVoid -> System.out.println("Mensaje actualizado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar Mensaje: " + e.getMessage()));
    }

    // Eliminar Mensaje
    public void eliminarMensaje(String chatId, String mensajeId) {
        firestore.collection("chats").document(chatId)
                .collection("mensajes").document(mensajeId).delete()
                .addOnSuccessListener(aVoid -> System.out.println("Mensaje eliminado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al eliminar Mensaje: " + e.getMessage()));
    }

    // Listar Mensajes de un Chat
    public void listarMensajes(String chatId) {
        firestore.collection("chats").document(chatId).collection("mensajes").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            Mensaje mensaje = document.toObject(Mensaje.class);
                            System.out.println(mensaje); // Mostrar datos del mensaje
                        });
                    } else {
                        System.err.println("Error al listar Mensajes: " + task.getException().getMessage());
                    }
                });
    }


    //// Mascota

    // Crear Mascota con Foto
    public void agregarMascota(Mascota mascota, Uri fotoUri) {
        String id = UUID.randomUUID().toString();
        mascota.setId(UUID.fromString(id));

        StorageReference storageRef = storage.getReference("fotos_mascotas/" + id + ".jpg");
        storageRef.putFile(fotoUri)
                .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    mascota.setFoto(uri.toString());
                    firestore.collection("mascotas").document(id).set(mascota)
                            .addOnSuccessListener(aVoid -> System.out.println("Mascota agregada correctamente."))
                            .addOnFailureListener(e -> System.err.println("Error al agregar Mascota: " + e.getMessage()));
                }))
                .addOnFailureListener(e -> System.err.println("Error al subir foto de Mascota a Storage: " + e.getMessage()));
    }

    // Editar Mascota (con opción de nueva foto)
    public void editarMascota(Mascota mascota, Uri nuevaFotoUri) {
        if (nuevaFotoUri != null) {
            StorageReference storageRef = storage.getReference("fotos_mascotas/" + mascota.getId() + ".jpg");
            storageRef.putFile(nuevaFotoUri)
                    .addOnSuccessListener(taskSnapshot -> storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        mascota.setFoto(uri.toString());
                        actualizarMascotaEnFirestore(mascota);
                    }))
                    .addOnFailureListener(e -> System.err.println("Error al subir nueva foto de Mascota: " + e.getMessage()));
        } else {
            actualizarMascotaEnFirestore(mascota);
        }
    }

    private void actualizarMascotaEnFirestore(Mascota mascota) {
        firestore.collection("mascotas").document(mascota.getId().toString()).set(mascota)
                .addOnSuccessListener(aVoid -> System.out.println("Mascota actualizada correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar Mascota: " + e.getMessage()));
    }

    // Eliminar Mascota y su Foto
    public void eliminarMascota(String id) {
        StorageReference storageRef = storage.getReference("fotos_mascotas/" + id + ".jpg");

        storageRef.delete()
                .addOnSuccessListener(aVoid -> firestore.collection("mascotas").document(id).delete()
                        .addOnSuccessListener(aVoid2 -> System.out.println("Mascota eliminada correctamente."))
                        .addOnFailureListener(e -> System.err.println("Error al eliminar Mascota en Firestore: " + e.getMessage())))
                .addOnFailureListener(e -> System.err.println("Error al eliminar foto de Mascota: " + e.getMessage()));
    }

    // Listar Mascotas
    public void listarMascotas() {
        firestore.collection("mascotas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            Mascota mascota = document.toObject(Mascota.class);
                            System.out.println(mascota); // Mostrar datos de la mascota
                        });
                    } else {
                        System.err.println("Error al listar Mascotas: " + task.getException().getMessage());
                    }
                });
    }

//// Geolocalizacion




    /// CHat

    // Crear Chat
    public void agregarChat(Chat chat) {
        String id = UUID.randomUUID().toString();
        chat.setId(UUID.fromString(id));

        firestore.collection("chats").document(id).set(chat)
                .addOnSuccessListener(aVoid -> System.out.println("Chat agregado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al agregar Chat: " + e.getMessage()));
    }

    // Editar Chat
    public void editarChat(Chat chat) {
        firestore.collection("chats").document(chat.getId().toString()).set(chat)
                .addOnSuccessListener(aVoid -> System.out.println("Chat actualizado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al actualizar Chat: " + e.getMessage()));
    }

    // Eliminar Chat y sus Mensajes
    public void eliminarChat(String chatId) {
        firestore.collection("chats").document(chatId).delete()
                .addOnSuccessListener(aVoid -> System.out.println("Chat eliminado correctamente."))
                .addOnFailureListener(e -> System.err.println("Error al eliminar Chat: " + e.getMessage()));
    }

    // Listar Chats
    public void listarChats() {
        firestore.collection("chats").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        task.getResult().forEach(document -> {
                            Chat chat = document.toObject(Chat.class);
                            System.out.println(chat); // Mostrar datos
                        });
                    } else {
                        System.err.println("Error al listar Chats: " + task.getException().getMessage());
                    }
                });
    }

}

