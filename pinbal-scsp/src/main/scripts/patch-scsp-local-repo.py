#!/usr/bin/env python3
"""
Patcheja scsp-core.jar a local-repo/ (i a ~/.m2/repository si hi és en caché)
eliminant les classes del namespace es/scsp/ que pinbal-scsp sobreescriu.

Les classes a eliminar es detecten dinàmicament llegint els fitxers font de
pinbal-scsp: qualsevol .java a src/main/java/es/scsp/ té prioritat sobre la
versió de la llibreria. Quan s'actualitza la versió de scsp-core (posant el
nou JAR a local-repo/), Maven detecta automàticament els nous conflictes i
aplica el patching sense cap modificació manual.

Comportament:
  - Idempotent: si el JAR ja no conté les classes conflictives, no fa res.
  - Backup: crea <nom>.original la primera vegada que patcheja un JAR concret.
  - Actualitza el checksum SHA1 del JAR patchejat si existeix el fitxer .sha1.

Ús: python3 patch-scsp-local-repo.py <source-dir> <local-repo-dir>
"""
import sys
import os
import glob
import zipfile
import io
import shutil
import hashlib


def sha1_of(data):
    return hashlib.sha1(data).hexdigest()


def patch_jar(jar_path, classes_to_remove):
    """
    Elimina del JAR les entrades de classe indicades (i els seus inner classes $N).
    Retorna True si el JAR ha estat modificat, False si ja estava net.
    """
    with zipfile.ZipFile(jar_path, 'r') as jr:
        jar_entries = {item.filename: item for item in jr.infolist()}

    # Expandim inner classes: ClassName$1.class, ClassName$Foo.class, etc.
    entries_to_delete = set()
    for cls in classes_to_remove:
        base = cls.removesuffix('.class')
        for entry in jar_entries:
            if entry == cls or (entry.startswith(base + '$') and entry.endswith('.class')):
                entries_to_delete.add(entry)

    if not entries_to_delete:
        return False  # Ja patchat o sense conflictes

    # Backup (només si no existeix encara)
    backup_path = jar_path + '.original'
    if not os.path.exists(backup_path):
        shutil.copy2(jar_path, backup_path)
        print(f"[patch-scsp] Backup: {backup_path}")

    print(f"[patch-scsp] Patchant {os.path.basename(jar_path)} ({len(entries_to_delete)} classes):")
    for entry in sorted(entries_to_delete):
        print(f"[patch-scsp]   - {entry}")

    # Reconstruim el JAR sense les classes conflictives
    buf = io.BytesIO()
    with zipfile.ZipFile(jar_path, 'r') as jr:
        with zipfile.ZipFile(buf, 'w', zipfile.ZIP_DEFLATED) as jw:
            for item in jr.infolist():
                if item.filename not in entries_to_delete:
                    jw.writestr(item, jr.read(item.filename))

    patched = buf.getvalue()
    with open(jar_path, 'wb') as f:
        f.write(patched)

    # Actualitzam el SHA1 si existeix
    sha1_path = jar_path + '.sha1'
    if os.path.exists(sha1_path):
        with open(sha1_path, 'w') as f:
            f.write(sha1_of(patched))

    return True


def collect_override_classes(source_dir):
    """
    Retorna el conjunt de paths de classe (.class) que corresponen als fitxers
    .java de pinbal-scsp sota el namespace es/scsp/.
    """
    scsp_src = os.path.join(source_dir, 'es', 'scsp')
    classes = set()
    if not os.path.isdir(scsp_src):
        return classes
    for root, _, files in os.walk(scsp_src):
        for f in files:
            if f.endswith('.java'):
                abs_path = os.path.join(root, f)
                rel = os.path.relpath(abs_path, source_dir)
                classes.add(rel.replace('.java', '.class').replace(os.sep, '/'))
    return classes


def patch_location(jar_pattern, classes_to_remove, label):
    jars = [j for j in glob.glob(jar_pattern) if not j.endswith('.original')]
    if not jars:
        return
    for jar_path in jars:
        patched = patch_jar(jar_path, classes_to_remove)
        if not patched:
            print(f"[patch-scsp] {label} {os.path.basename(jar_path)}: ja patchat, res a fer.")


def main(source_dir, local_repo_dir):
    classes = collect_override_classes(source_dir)
    if not classes:
        print("[patch-scsp] Cap font es/scsp/ a pinbal-scsp, no cal patchejar.")
        return

    print(f"[patch-scsp] Classes sobreescrites a pinbal-scsp ({len(classes)}):")
    for c in sorted(classes):
        print(f"[patch-scsp]   {c}")

    # Patcham scsp-core a local-repo/
    local_core_pattern = os.path.join(local_repo_dir, 'es', 'scsp', 'scsp-core', '*', 'scsp-core-*.jar')
    patch_location(local_core_pattern, classes, "local-repo (scsp-core)")

    # Patcham scsp-beans a local-repo/ (peticion/ObjectFactory vs respuesta/ObjectFactory)
    local_beans_pattern = os.path.join(local_repo_dir, 'es', 'scsp', 'scsp-beans', '*', 'scsp-beans-*.jar')
    patch_location(local_beans_pattern, classes, "local-repo (scsp-beans)")

    # Patcham scsp-core a ~/.m2/repository/ si hi és en caché
    m2_core_pattern = os.path.join(os.path.expanduser('~'), '.m2', 'repository',
                              'es', 'scsp', 'scsp-core', '*', 'scsp-core-*.jar')
    patch_location(m2_core_pattern, classes, "~/.m2 (scsp-core)")

    # Patcham scsp-beans a ~/.m2/repository/ si hi és en caché
    m2_beans_pattern = os.path.join(os.path.expanduser('~'), '.m2', 'repository',
                              'es', 'scsp', 'scsp-beans', '*', 'scsp-beans-*.jar')
    patch_location(m2_beans_pattern, classes, "~/.m2 (scsp-beans)")


if __name__ == '__main__':
    if len(sys.argv) != 3:
        print(f"Ús: {sys.argv[0]} <source-dir> <local-repo-dir>")
        sys.exit(1)
    main(sys.argv[1], sys.argv[2])
