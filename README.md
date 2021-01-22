# Feistel Cipher Java implementation

This program is an implementation of Feistel Cipher using ECB (Electronic Codebook), CBC (Cipher Block Chaining), and OFB (Output Feedback) encryption modes. The fixed variables of the Feistel cipher are:

• 10 rounds of encryption/decryption

• 96 bit block size

• 96 bit key size






The program now runs with command line arguments, which has this structure:

BBMcrypt enc|dec -K key -I input -O output –M mode

• enc|dec specifies the action, which can be either encryption or decryption

• -K key specifies the file name that contains encryption/decryption key, which is
encoded in base64 encoding

• -I input specifies the input file name, which can be either plaintext or ciphertext

• -O output specifies the output file name, which can be either plaintext or ciphertext

• -M mode specifies the encryption mode, which can be ECB, CBC or OFB. 


Tested with multiple input-output files, no errors.
