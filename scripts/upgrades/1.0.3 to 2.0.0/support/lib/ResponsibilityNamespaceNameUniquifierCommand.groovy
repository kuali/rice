import java.sql.Connection

class ResponsibilityNamespaceNameUniquifierCommand extends RecordUniquifier {
    def ResponsibilityNamespaceNameUniquifierCommand() {
        super("KRIM_RSP_T", "RSP_ID", {
          self, row ->
            [ NM: row['NM'] + " " + row[self.pk_col] ]
        }, ["NMSPC_CD", "NM"])
    }
}