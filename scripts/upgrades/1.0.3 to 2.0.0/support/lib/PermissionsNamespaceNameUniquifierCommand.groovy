import java.sql.Connection

/**
 * DbCommand that emits SQL that will append the primary key to NM col of any KRIM_PERM_T
 * records with duplicate (NMSPC_CD,NM) fields
 */
class PermissionsNamespaceNameUniquifierCommand extends RecordUniquifier {
    def PermissionsNamespaceNameUniquifierCommand() {
        super("KRIM_PERM_T", "PERM_ID", {
          self, row ->
            [ NM: row['NM'] + " " + row[self.pk_col] ]
        }, ["NMSPC_CD", "NM"])
    }
}