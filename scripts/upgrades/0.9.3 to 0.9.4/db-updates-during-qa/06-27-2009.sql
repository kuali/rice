-- Fixes an issue with our sample eDoc Lite document

UPDATE KREW_DOC_TYP_T SET SVC_NMSPC=NULL WHERE SVC_NMSPC='FooBar'
/