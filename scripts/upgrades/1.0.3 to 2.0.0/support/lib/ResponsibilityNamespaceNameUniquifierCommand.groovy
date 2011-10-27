import java.sql.Connection

/**
 * DbCommand that emits SQL that will append the primary key to NM col of any KRIM_RSP_T
 * records with duplicate (NMSPC_CD,NM) fields
 */
class ResponsibilityNamespaceNameUniquifierCommand extends RecordUniquifier {
    def ResponsibilityNamespaceNameUniquifierCommand() {
        super("KRIM_RSP_T", "RSP_ID", {
          self, row ->
            [ NM: row['NM'] + " " + row[self.pk_col] ]
        }, ["NMSPC_CD", "NM"])
    }
}