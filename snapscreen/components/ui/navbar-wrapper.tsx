"use client";

import { useEffect, useState } from "react";
import { User } from "firebase/auth";
import { onAuthStateChanged, signOut as firebaseSignOut } from "firebase/auth";

import { auth } from "@/lib/firebase";
import { Navbar } from "@/components/ui/navbar";

export function NavbarWrapper() {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const unsubscribe = onAuthStateChanged(auth, (currentUser) => {
      setUser(currentUser);
      setLoading(false);
    });

    return () => unsubscribe();
  }, []);

  const signOut = async () => {
    try {
      await firebaseSignOut(auth);
    } catch (error) {
      console.error("Error signing out:", error);
    }
  };

  if (loading) {
    // You could return a skeleton here if preferred
    return null;
  }

  return <Navbar user={user} signOut={signOut} />;
} 