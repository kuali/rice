import java.sql.Connection

class PermissionsNamespaceNameUniquifierCommand extends RecordUniquifier {
    def PermissionsNamespaceNameUniquifierCommand() {
        super("KRIM_PERM_T", "PERM_ID", {
          self, row ->
            [ NM: row['NM'] + " " + row[self.pk_col] ]
        }, ["NMSPC_CD", "NM"])
    }
}